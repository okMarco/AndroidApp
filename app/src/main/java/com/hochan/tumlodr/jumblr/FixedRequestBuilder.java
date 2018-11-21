package com.hochan.tumlodr.jumblr;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.tumblr.jumblr.JumblrClient;
import com.tumblr.jumblr.exceptions.JumblrException;
import com.tumblr.jumblr.request.MultipartConverter;
import com.tumblr.jumblr.request.RequestBuilder;
import com.tumblr.jumblr.responses.JsonElementDeserializer;
import com.tumblr.jumblr.responses.ResponseWrapper;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.TumblrApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;
import org.scribe.utils.Preconditions;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.util.Map;

/**
 * .
 * Created by hochan on 2018/6/19.
 */

public class FixedRequestBuilder {

	private Token token;
	private OAuthService service;
	private String hostname = "api.tumblr.com";
	private String xauthEndpoint = "https://www.tumblr.com/oauth/access_token";
	private final JumblrClient client;

	public FixedRequestBuilder(JumblrClient client) {
		this.client = client;
	}

	public String getRedirectUrl(String path) {
		OAuthRequest request = this.constructGet(path, null);
		sign(request);
		boolean presetVal = HttpURLConnection.getFollowRedirects();
		HttpURLConnection.setFollowRedirects(false);
		Response response = request.send();
		HttpURLConnection.setFollowRedirects(presetVal);
		if (response.getCode() == 301) {
			return response.getHeader("Location");
		} else {
			throw new JumblrException(response);
		}
	}

	public ResponseWrapper postMultipart(String path, Map<String, ?> bodyMap) throws IOException {
		OAuthRequest request = this.constructPost(path, bodyMap);
		sign(request);
		OAuthRequest newRequest = RequestBuilder.convertToMultipart(request, bodyMap);
		return clear(newRequest.send());
	}

	public ResponseWrapper post(String path, Map<String, ?> bodyMap) {
		OAuthRequest request = this.constructPost(path, bodyMap);
		sign(request);
		return clear(request.send());
	}

	/**
	 * Posts an XAuth request. A new method is needed because the response from
	 * the server is not a standard Tumblr JSON response.
	 *
	 * @param email    the user's login email.
	 * @param password the user's password.
	 * @return the login token.
	 */
	public Token postXAuth(final String email, final String password) {
		OAuthRequest request = constructXAuthPost(email, password);
		setToken("", ""); // Empty token is required for Scribe to execute XAuth.
		sign(request);
		return clearXAuth(request.send());
	}

	// Construct an XAuth request
	private OAuthRequest constructXAuthPost(String email, String password) {
		OAuthRequest request = new OAuthRequest(Verb.POST, xauthEndpoint);
		request.addBodyParameter("x_auth_username", email);
		request.addBodyParameter("x_auth_password", password);
		request.addBodyParameter("x_auth_mode", "client_auth");
		return request;
	}

	public ResponseWrapper get(String path, Map<String, ?> map) {
		OAuthRequest request = this.constructGet(path, map);
		sign(request);
		return clear(request.send());
	}

	public OAuthRequest constructGet(String path, Map<String, ?> queryParams) {
		String url = "https://" + hostname + "/v2" + path;
		OAuthRequest request = new OAuthRequest(Verb.GET, url);
		if (queryParams != null) {
			for (Map.Entry<String, ?> entry : queryParams.entrySet()) {
				request.addQuerystringParameter(entry.getKey(), entry.getValue().toString());
			}
		}
		return request;
	}

	private OAuthRequest constructPost(String path, Map<String, ?> bodyMap) {
		String url = "https://" + hostname + "/v2" + path;
		OAuthRequest request = new OAuthRequest(Verb.POST, url);

		for (Map.Entry<String, ?> entry : bodyMap.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			if (value == null || value instanceof File) {
				continue;
			}
			request.addBodyParameter(key, value.toString());
		}
		return request;
	}

	public void setConsumer(String consumerKey, String consumerSecret) {
		service = new ServiceBuilder().
				provider(TumblrApi.class).
				apiKey(consumerKey).apiSecret(consumerSecret).
				build();
	}

	public void setToken(String token, String tokenSecret) {
		this.token = new Token(token, tokenSecret);
	}

	public void setToken(final Token token) {
		this.token = token;
	}

	/* package-visible for testing */ ResponseWrapper clear(Response response) {
		if (response.getCode() == 200 || response.getCode() == 201) {
			String json = getStreamContents(response.getStream());
			try {
				Gson gson = new GsonBuilder().
						registerTypeAdapter(JsonElement.class, new JsonElementDeserializer()).
						create();
				ResponseWrapper wrapper = gson.fromJson(json, ResponseWrapper.class);
				if (wrapper == null) {
					throw new JumblrException(response);
				}
				wrapper.setClient(client);
				return wrapper;
			} catch (JsonSyntaxException ex) {
				throw new JumblrException(response);
			}
		} else {
			throw new JumblrException(response);
		}
	}

	private Token parseXAuthResponse(final Response response) {
		String responseStr = response.getBody();
		if (responseStr != null) {
			// Response is received in the format "oauth_token=value&oauth_token_secret=value".
			String extractedToken = null, extractedSecret = null;
			final String[] values = responseStr.split("&");
			for (String value : values) {
				final String[] kvp = value.split("=");
				if (kvp != null && kvp.length == 2) {
					if (kvp[0].equals("oauth_token")) {
						extractedToken = kvp[1];
					} else if (kvp[0].equals("oauth_token_secret")) {
						extractedSecret = kvp[1];
					}
				}
			}
			if (extractedToken != null && extractedSecret != null) {
				return new Token(extractedToken, extractedSecret);
			}
		}
		// No good
		throw new JumblrException(response);
	}

	/* package-visible for testing */ Token clearXAuth(Response response) {
		if (response.getCode() == 200 || response.getCode() == 201) {
			return parseXAuthResponse(response);
		} else {
			throw new JumblrException(response);
		}
	}

	private void sign(OAuthRequest request) {
		if (token != null) {
			service.signRequest(token, request);
		}
	}

	public static OAuthRequest convertToMultipart(OAuthRequest request, Map<String, ?> bodyMap) throws IOException {
		return new MultipartConverter(request, bodyMap).getRequest();
	}

	public String getHostname() {
		return hostname;
	}

	/**
	 * Set hostname without protocol
	 *
	 * @param host such as "api.tumblr.com"
	 */
	public void setHostname(String host) {
		this.hostname = host;
	}

	@SuppressWarnings("UnusedAssignment")
	public static String getStreamContents(InputStream is) {
		Preconditions.checkNotNull(is, "Cannot get String from a null object");
		try {
			final char[] buffer = new char[0x10000];
			StringBuilder out = new StringBuilder();
			Reader in = new InputStreamReader(is, "UTF-8");
			int read;
			do {
				read = in.read(buffer, 0, buffer.length);
				if (read > 0) {
					try {
						out.append(buffer, 0, read);
					} catch (OutOfMemoryError error) {
						in.close();
						in = null;
						out = null;
						return null;
					}
				}
			} while (read >= 0);
			in.close();
			return out.toString();
		} catch (InterruptedIOException e) {
			Crashlytics.logException(e);
			throw new IllegalStateException(e.getMessage());
		} catch (IOException ioe) {
			Crashlytics.logException(ioe);
			throw new IllegalStateException("Error while reading response body", ioe);
		} catch (OutOfMemoryError error) {
			Crashlytics.logException(error);
			throw new IllegalStateException(error.getMessage());
		}
	}
}

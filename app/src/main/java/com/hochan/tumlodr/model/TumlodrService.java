package com.hochan.tumlodr.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.hochan.tumlodr.TumlodrApp;
import com.hochan.tumlodr.jumblr.FixedJumblrClient;
import com.hochan.tumlodr.model.sql.FollowingsBlogDBController;
import com.hochan.tumlodr.module.search.SearchBlogInfo;
import com.hochan.tumlodr.module.search.SearchPost;
import com.hochan.tumlodr.module.search.SearchResult;
import com.hochan.tumlodr.tools.AppConfig;
import com.hochan.tumlodr.util.Events;
import com.hochan.tumlodr.util.RxBus;
import com.tumblr.jumblr.JumblrClient;
import com.tumblr.jumblr.types.Blog;
import com.tumblr.jumblr.types.Post;
import com.tumblr.jumblr.types.User;

import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Utf8;

/**
 * .
 * Created by Administrator on 2016/5/22.
 */

public class TumlodrService {

	public static final String REQUEST_SINCE_ID = "since_id";
	public static final String REQUEST_AFTER = "after";
	public static final String REQUEST_OFFSET = "offset";
	public static final String REQUEST_BEFORE = "before";
	private static final String REQUEST_TYPE = "type";

	private static User mCurrUser;

	private static CookieJar sCookieJar = new CookieJar() {
		private final Map<String, List<Cookie>> COOKIE_MAP = new HashMap<>();

		@Override
		public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
			COOKIE_MAP.put(url.host(), cookies);
		}

		@SuppressWarnings("unchecked")
		@Override
		public List<Cookie> loadForRequest(HttpUrl url) {
			return COOKIE_MAP.get(url.host()) == null ? Collections.EMPTY_LIST
					: COOKIE_MAP.get(url.host());
		}
	};

	private static final class HOLDER {
		private static JumblrClient INSTANCE = new FixedJumblrClient();
	}

	public static Observable<User> getCurrUserFromServer() {
		return Observable.create(new ObservableOnSubscribe<User>() {
			@Override
			public void subscribe(@NonNull ObservableEmitter<User> e) throws Exception {
				JumblrClient client = HOLDER.INSTANCE;
				mCurrUser = client.user();
				e.onNext(mCurrUser);
			}
		}).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	public static Observable<List<Post>> loadDashBoardPosts(final String param, final long value, final String type) {
		return Observable.create(new ObservableOnSubscribe<List<Post>>() {
			@Override
			public void subscribe(@NonNull ObservableEmitter<List<Post>> e) throws Exception {
				JumblrClient client = HOLDER.INSTANCE;
				Map<String, Object> requestParams = new HashMap<>();
				if (value != 0) {
					requestParams.put(param, value);
				}
				requestParams.put("notes_info", true);
				if (type != null) {
					requestParams.put(REQUEST_TYPE, type);
				}
				List<Post> resultPostList = client.userDashboard(requestParams);
				e.onNext(resultPostList);
			}
		}).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	public static Observable<List<Post>> loadLikePagePosts(final String param, final long value) {
		return Observable.create(new ObservableOnSubscribe<List<Post>>() {
			@Override
			public void subscribe(@NonNull ObservableEmitter<List<Post>> e) throws Exception {
				JumblrClient client = HOLDER.INSTANCE;
				Map<String, Object> requestParams = new HashMap<>();
				if (value != 0) {
					requestParams.put(param, value);
				}
				requestParams.put("notes_info", true);
				e.onNext(client.userLikes(requestParams));
			}
		}).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	public static Observable<List<Post>> loadBlogPosts(final String blogName, final String param, final long value, final String type) {
		return Observable.create(new ObservableOnSubscribe<List<Post>>() {
			@Override
			public void subscribe(@NonNull ObservableEmitter<List<Post>> e) throws Exception {
				Map<String, Object> requestParams = new HashMap<>();
				if (value != 0) {
					requestParams.put(param, value);
				}
				if (!TextUtils.isEmpty(type)) {
					requestParams.put(REQUEST_TYPE, type);
				}
				requestParams.put("notes_info", true);
				JumblrClient client = HOLDER.INSTANCE;
				List<Post> posts = client.blogPosts(blogName + ".tumblr.com", requestParams);
				e.onNext(posts);
			}
		}).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	public static Observable<List<Post>> loadBlogLikePosts(final String blogName, final String param, final long value) {
		return Observable.create(new ObservableOnSubscribe<List<Post>>() {
			@Override
			public void subscribe(@NonNull ObservableEmitter<List<Post>> e) throws Exception {
				JumblrClient client = HOLDER.INSTANCE;
				if (value == 0) {
					e.onNext(client.blogLikes(blogName));
				}

				Map<String, Object> requestParams = new HashMap<>();
				requestParams.put(param, value);
				requestParams.put("notes_info", true);
				e.onNext(client.blogLikes(blogName, requestParams));
			}
		}).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	public static Observable<List<Post>> loadTaggedPosts(final String tag, final long before) {
		return Observable.create(new ObservableOnSubscribe<List<Post>>() {
			@Override
			public void subscribe(ObservableEmitter<List<Post>> e) throws Exception {
				Map<String, Object> params = new HashMap<>();
				params.put("before", before);
				e.onNext(HOLDER.INSTANCE.tagged(tag, params));

			}
		}).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	public static void setPostLiked(Post post, boolean liked) {
		System.out.println(((Post) post).getClass());
		try {
			Field like = (Post.class.getDeclaredField("liked"));
			like.setAccessible(true);
			like.set(post, liked);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	public static Observable<Long> likePost(final long postId, final String reblogKey) {
		return Observable.create(new ObservableOnSubscribe<Long>() {
			@Override
			public void subscribe(@NonNull ObservableEmitter<Long> e) throws Exception {
				JumblrClient client = HOLDER.INSTANCE;
				client.like(postId, reblogKey);
				e.onNext(postId);
			}
		}).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	public static Observable<Post> unlikePost(final Post post) {
		return Observable.create(new ObservableOnSubscribe<Post>() {
			@Override
			public void subscribe(@NonNull ObservableEmitter<Post> e) throws Exception {
				post.unlike();
				e.onNext(post);
			}
		}).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	public static void reblog(final String blogName, final long postId, final String reblogKey) {
		Observable.create(new ObservableOnSubscribe<Post>() {
			@Override
			public void subscribe(@NonNull ObservableEmitter<Post> e) throws Exception {
				JumblrClient client = HOLDER.INSTANCE;
				e.onNext(client.postReblog(blogName + ".tumblr.com", postId, reblogKey));
			}
		}).subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new BaseObserver<Post>() {
					@Override
					public void onError(Throwable throwable) {
						if (throwable instanceof NullPointerException) {
							RxBus.getInstance().send(new Events<>(Events.EVENT_CODE_REBLOG_SUCCESS, postId));
							return;
						}
						throwable.printStackTrace();
						RxBus.getInstance().send(new Events<>(Events.EVENT_CODE_REBLOG_FAILE, throwable));
					}

					@Override
					public void onNext(Post post) {
						RxBus.getInstance().send(new Events<>(Events.EVENT_CODE_REBLOG_SUCCESS, post));
					}
				});
	}

	public static Observable<Blog> getBlogInfo(final String blogName) {
		return Observable.create(new ObservableOnSubscribe<Blog>() {
			@Override
			public void subscribe(@NonNull ObservableEmitter<Blog> e) throws Exception {
				e.onNext(HOLDER.INSTANCE.blogInfo(blogName));
			}
		}).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	public static Observable<Object> followBlog(final String blogName) {
		return Observable.create(new ObservableOnSubscribe<Object>() {
			@Override
			public void subscribe(@NonNull ObservableEmitter<Object> e) throws Exception {
				HOLDER.INSTANCE.follow(blogName);
			}
		}).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	public static void unFollowBlog(final String blogName) {
		Observable.create(new ObservableOnSubscribe<Object>() {
			@Override
			public void subscribe(@NonNull ObservableEmitter<Object> e) throws Exception {
				HOLDER.INSTANCE.unfollow(blogName);
			}
		}).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe();
	}

	public static Observable<List<Blog>> refreshUserFollowings() {
		return Observable.create(new ObservableOnSubscribe<List<Blog>>() {
			@Override
			public void subscribe(@NonNull ObservableEmitter<List<Blog>> e) throws Exception {
				Map<String, Object> param = new HashMap<>();
				param.put("offset", 0);
				List<Blog> blogList = HOLDER.INSTANCE.userFollowing(param);

				FollowingsBlogDBController controller = new FollowingsBlogDBController();
				controller.addNewBlogs(blogList);
				e.onNext(controller.getAllBlogs(0));
			}
		}).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	public static Observable<List<Blog>> getUserFollowing(final int offset) {
		return Observable.create(new ObservableOnSubscribe<List<Blog>>() {
			@Override
			public void subscribe(ObservableEmitter<List<Blog>> e) throws Exception {
				FollowingsBlogDBController followingsBlogDBController = new FollowingsBlogDBController();
				List<Blog> userFollowings = followingsBlogDBController.getAllBlogs(offset);
				if (userFollowings.isEmpty()) {
					JumblrClient client = HOLDER.INSTANCE;
					Map<String, Object> param = new HashMap<>();
					param.put("offset", offset);
					userFollowings = client.userFollowing(param);
					System.out.println(userFollowings.size());
					followingsBlogDBController.addBlogs(userFollowings);
				}
				e.onNext(userFollowings);
			}
		}).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	public static Observable<SearchResult> searchFirstPage(final String key) {
		return Observable.create(new ObservableOnSubscribe<SearchResult>() {
			@SuppressWarnings("ConstantConditions")
			@Override
			public void subscribe(ObservableEmitter<SearchResult> e) throws Exception {
				OkHttpClient okHttpClient = new OkHttpClient.Builder().cookieJar(sCookieJar).build();
				String html = okHttpClient.newCall(new Request.Builder().get()
						.url("https://www.tumblr.com/search/" + URLEncoder.encode(key, "utf-8") + "/post_page/1").build())
						.execute().body().string();

				SearchResult searchResult = new SearchResult();
				Document doc = Jsoup.parse(html);
				Element formKey = doc.getElementById("tumblr_form_key");
				if (formKey != null) {
					searchResult.setFormKey(formKey.attr("content"));
				}

				Elements searchJsonElements = doc.getElementsByAttribute("data-search-tumblelogs-json");
				if (searchJsonElements != null && searchJsonElements.size() > 0) {
					String json = StringEscapeUtils.unescapeHtml4(searchJsonElements.get(0).attr("data-search-tumblelogs-json"));
					List<SearchBlogInfo> searchBlogInfoList = new Gson().fromJson(json, new TypeToken<List<SearchBlogInfo>>() {
					}.getType());
					searchResult.setSearchBlogInfoList(searchBlogInfoList);
					e.onNext(searchResult);
				}
				Element searchPostContainer = doc.getElementById("search_posts");
				if (searchPostContainer != null) {
					Elements postList = searchPostContainer.children();
					if (postList != null && postList.size() > 0) {
						List<Post> posts = new ArrayList<>();
						for (Element postElement : postList) {
							try {
								String postJson = postElement.attr("data-json");
								JsonObject jsonObject = new JsonParser().parse(postJson).getAsJsonObject();
								String id = jsonObject.get("id").getAsString();
								String name = jsonObject.get("tumblelog-name").getAsString();
								Post post = HOLDER.INSTANCE.blogPost(name, Long.valueOf(id));
								posts.add(post);
							} catch (Exception exception) {
								exception.printStackTrace();
							}
						}
						searchResult.setSearchPostList(posts);
						searchResult.setSearchBlogInfoList(null);
						e.onNext(searchResult);
					}
				}
			}
		}).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	public static Observable<List<Post>> searchNextPageHtml(final String key, final int page,
	                                                        final int currentSize, final String formKey) {
		return Observable.create(new ObservableOnSubscribe<List<Post>>() {
			@SuppressWarnings("ConstantConditions")
			@Override
			public void subscribe(ObservableEmitter<List<Post>> e) throws Exception {
				OkHttpClient okHttpClient = new OkHttpClient.Builder().cookieJar(sCookieJar).build();
				FormBody formBody = new FormBody.Builder()
						.add("q", URLEncoder.encode(key, "utf-8"))
						.add("sort", "top")
						.add("post_view", "masonry")
						.add("blogs_before", "11")
						.add("num_blogs_shown", "8")
						.add("num_posts_shown", "60")
						.add("before", String.valueOf(currentSize))
						.add("blog_page", "2")
						.add("safe_mode", "false")
						.add("post_page", String.valueOf(page))
						.add("filter_nsfw", "true")
						.add("filter_post_type", "")
						.add("next_ad_offset", "0")
						.add("ad_placement_id", "0")
						.add("more_posts", "true").build();
				Headers headers = new Headers.Builder()
						.add("accept-encoding", "gzip")
						.add("accept", "application/json, text/javascript, */*; q=0.01")
						.add("content-type", "application/x-www-form-urlencoded; charset=UTF-8")
						.add("origin", "https://www.tumblr.com")
						.add("referer", "https://www.tumblr.com/search/" +
								URLEncoder.encode(key, "utf-8"))
						.add("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36")
						.add("x-requested-with", "XMLHttpRequest")
						.add("x-tumblr-form-key", formKey).build();
				Response response = okHttpClient.newCall(new Request.Builder().post(formBody)
						.headers(headers)
						.url("https://www.tumblr.com/search/" + URLEncoder.encode(key, "utf-8") + "/post_page/" + page).
								build()).execute();
				System.out.println("TumlodrService" + ":" + "subscribe" + " --> " + response.headers());
				String json = response.body().string();
				e.onNext(null);
			}
		}).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}
}

package com.hochan.tumlodr.jumblr;

import android.content.SharedPreferences;
import android.widget.Toast;

import com.hochan.tumlodr.TumlodrApp;
import com.hochan.tumlodr.tools.AppConfig;
import com.tumblr.jumblr.JumblrClient;
import com.tumblr.jumblr.types.Blog;
import com.tumblr.jumblr.types.Post;
import com.tumblr.jumblr.types.User;

import org.scribe.model.Token;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * .
 * Created by hochan on 2018/6/19.
 */

public class FixedJumblrClient extends JumblrClient {

	private FixedRequestBuilder mFixedRequestBuilder;
	private String mApiKey;

	public FixedJumblrClient() {
		mFixedRequestBuilder = new FixedRequestBuilder(this);
		SharedPreferences sharedPreferences = TumlodrApp.getContext().getSharedPreferences(AppConfig.SHARE_USER, MODE_PRIVATE);
		final String consumerKey = sharedPreferences.getString(AppConfig.SHARE_CUSTOM_CONSUMER_KEY, AppConfig.CONSUMER_KEY);
		final String secretKey = sharedPreferences.getString(AppConfig.SHARE_CUSTOM_SECRET_KEY, AppConfig.CONSUMER_SECRET);
		mFixedRequestBuilder = new FixedRequestBuilder(this);
		mFixedRequestBuilder.setConsumer(consumerKey, secretKey);
		mApiKey = consumerKey;
		if (TumlodrApp.sOAuthToken == null || TumlodrApp.sOAuthTokenSecret == null) {
			TumlodrApp.sOAuthToken = sharedPreferences.getString(AppConfig.SHARE_USER_OAUTH_TOKEN, null);
			TumlodrApp.sOAuthTokenSecret = sharedPreferences.getString(AppConfig.SHARE_USER_OAUTH_TOKEN_SECRET, null);
		}
		if (TumlodrApp.sOAuthToken != null && TumlodrApp.sOAuthTokenSecret != null) {
			setToken(TumlodrApp.sOAuthToken, TumlodrApp.sOAuthTokenSecret);
		} else {
			Toast.makeText(TumlodrApp.getContext(), "Invalid oauth token. Try Login in again.", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Set the token for this client
	 *
	 * @param token       The token for the client
	 * @param tokenSecret The token secret for the client
	 */
	public void setToken(String token, String tokenSecret) {
		this.mFixedRequestBuilder.setToken(token, tokenSecret);
	}

	/**
	 * Set the token for this client.
	 *
	 * @param token The token for the client.
	 */
	public void setToken(final Token token) {
		this.mFixedRequestBuilder.setToken(token);
	}

	/**
	 * Performs an XAuth authentication.
	 *
	 * @param email    the user's login email.
	 * @param password the user's login password.
	 */
	public void xauth(final String email, final String password) {
		setToken(this.mFixedRequestBuilder.postXAuth(email, password));
	}

	/**
	 * Get the user info for the authenticated User
	 *
	 * @return The authenticated user
	 */
	public User user() {
		return mFixedRequestBuilder.get("/user/info", null).getUser();
	}

	/**
	 * Get the user dashboard for the authenticated User
	 *
	 * @param options the options for the call (or null)
	 * @return A List of posts
	 */
	public List<Post> userDashboard(Map<String, ?> options) {
		return mFixedRequestBuilder.get("/user/dashboard", options).getPosts();
	}

	public List<Post> userDashboard() {
		return this.userDashboard(null);
	}

	/**
	 * Get the blogs the given user is following
	 *
	 * @return a List of blogs
	 */
	public List<Blog> userFollowing(Map<String, ?> options) {
		return mFixedRequestBuilder.get("/user/following", options).getBlogs();
	}

	public List<Blog> userFollowing() {
		return this.userFollowing(null);
	}

	/**
	 * Tagged posts
	 *
	 * @param tag     the tag to search
	 * @param options the options for the call (or null)
	 * @return a list of posts
	 */
	public List<Post> tagged(String tag, Map<String, ?> options) {
		if (options == null) {
			options = Collections.emptyMap();
		}
		Map<String, Object> soptions = FixedJumblrClient.safeOptionMap(options);
		soptions.put("api_key", mApiKey);
		soptions.put("tag", tag);
		return mFixedRequestBuilder.get("/tagged", soptions).getTaggedPosts();
	}

	public List<Post> tagged(String tag) {
		return this.tagged(tag, null);
	}

	/**
	 * Get the blog info for a given blog
	 *
	 * @param blogName the Name of the blog
	 * @return The Blog object for this blog
	 */
	public Blog blogInfo(String blogName) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("api_key", this.mApiKey);
		return mFixedRequestBuilder.get(blogPath(blogName, "/info"), map).getBlog();
	}

	/**
	 * Get the followers for a given blog
	 *
	 * @param blogName the name of the blog
	 * @return the blog object for this blog
	 */
	public List<User> blogFollowers(String blogName, Map<String, ?> options) {
		return mFixedRequestBuilder.get(blogPath(blogName, "/followers"), options).getUsers();
	}

	public List<User> blogFollowers(String blogName) {
		return this.blogFollowers(blogName, null);
	}

	/**
	 * Get the public likes for a given blog
	 *
	 * @param blogName the name of the blog
	 * @param options  the options for this call (or null)
	 * @return a List of posts
	 */
	public List<Post> blogLikes(String blogName, Map<String, ?> options) {
		if (options == null) {
			options = Collections.emptyMap();
		}
		Map<String, Object> soptions = safeOptionMap(options);
		soptions.put("api_key", this.mApiKey);
		return mFixedRequestBuilder.get(blogPath(blogName, "/likes"), soptions).getLikedPosts();
	}

	public List<Post> blogLikes(String blogName) {
		return this.blogLikes(blogName, null);
	}

	/**
	 * Get the posts for a given blog
	 *
	 * @param blogName the name of the blog
	 * @param options  the options for this call (or null)
	 * @return a List of posts
	 */
	public List<Post> blogPosts(String blogName, Map<String, ?> options) {
		if (options == null) {
			options = Collections.emptyMap();
		}
		Map<String, Object> soptions = safeOptionMap(options);
		soptions.put("api_key", mApiKey);

		String path = "/posts";
		if (soptions.containsKey("type")) {
			path += "/" + soptions.get("type").toString();
			soptions.remove("type");
		}
		return mFixedRequestBuilder.get(blogPath(blogName, path), soptions).getPosts();
	}

	public List<Post> blogPosts(String blogName) {
		return this.blogPosts(blogName, null);
	}

	/**
	 * Get an individual post by id
	 *
	 * @param blogName the name of the blog
	 * @param postId   the id of the post to get
	 * @return the Post or null
	 */
	public Post blogPost(String blogName, Long postId) {
		HashMap<String, String> options = new HashMap<String, String>();
		options.put("id", postId.toString());
		List<Post> posts = this.blogPosts(blogName, options);
		return posts.size() > 0 ? posts.get(0) : null;
	}

	/**
	 * Get the queued posts for a given blog
	 *
	 * @param blogName the name of the blog
	 * @param options  the options for this call (or null)
	 * @return a List of posts
	 */
	public List<Post> blogQueuedPosts(String blogName, Map<String, ?> options) {
		return mFixedRequestBuilder.get(blogPath(blogName, "/posts/queue"), options).getPosts();
	}

	public List<Post> blogQueuedPosts(String blogName) {
		return this.blogQueuedPosts(blogName, null);
	}

	/**
	 * Get the draft posts for a given blog
	 *
	 * @param blogName the name of the blog
	 * @param options  the options for this call (or null)
	 * @return a List of posts
	 */
	public List<Post> blogDraftPosts(String blogName, Map<String, ?> options) {
		return mFixedRequestBuilder.get(blogPath(blogName, "/posts/draft"), options).getPosts();
	}

	public List<Post> blogDraftPosts(String blogName) {
		return this.blogDraftPosts(blogName, null);
	}

	/**
	 * Get the submissions for a given blog
	 *
	 * @param blogName the name of the blog
	 * @param options  the options for this call (or null)
	 * @return a List of posts
	 */
	public List<Post> blogSubmissions(String blogName, Map<String, ?> options) {
		return mFixedRequestBuilder.get(blogPath(blogName, "/posts/submission"), options).getPosts();
	}

	public List<Post> blogSubmissions(String blogName) {
		return this.blogSubmissions(blogName, null);
	}

	/**
	 * Get the likes for the authenticated user
	 *
	 * @param options the options for this call (or null)
	 * @return a List of posts
	 */
	public List<Post> userLikes(Map<String, ?> options) {
		return mFixedRequestBuilder.get("/user/likes", options).getLikedPosts();
	}

	public List<Post> userLikes() {
		return this.userLikes(null);
	}

	/**
	 * Get a specific size avatar for a given blog
	 *
	 * @param blogName the avatar URL of the blog
	 * @param size     The size requested
	 * @return a string representing the URL of the avatar
	 */
	public String blogAvatar(String blogName, Integer size) {
		String pathExt = size == null ? "" : "/" + size.toString();
		return mFixedRequestBuilder.getRedirectUrl(blogPath(blogName, "/avatar" + pathExt));
	}

	public String blogAvatar(String blogName) {
		return this.blogAvatar(blogName, null);
	}

	/**
	 * Like a given post
	 *
	 * @param postId    the ID of the post to like
	 * @param reblogKey The reblog key for the post
	 */
	public void like(Long postId, String reblogKey) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("id", postId.toString());
		map.put("reblog_key", reblogKey);
		mFixedRequestBuilder.post("/user/like", map);
	}

	/**
	 * Unlike a given post
	 *
	 * @param postId    the ID of the post to unlike
	 * @param reblogKey The reblog key for the post
	 */
	public void unlike(Long postId, String reblogKey) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("id", postId.toString());
		map.put("reblog_key", reblogKey);
		mFixedRequestBuilder.post("/user/unlike", map);
	}

	/**
	 * Follow a given blog
	 *
	 * @param blogName The name of the blog to follow
	 */
	public void follow(String blogName) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("url", blogUrl(blogName));
		mFixedRequestBuilder.post("/user/follow", map);
	}

	/**
	 * Unfollow a given blog
	 *
	 * @param blogName the name of the blog to unfollow
	 */
	public void unfollow(String blogName) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("url", blogUrl(blogName));
		mFixedRequestBuilder.post("/user/unfollow", map);
	}

	/**
	 * Delete a given post
	 *
	 * @param blogName the name of the blog the post is in
	 * @param postId   the id of the post to delete
	 */
	public void postDelete(String blogName, Long postId) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("id", postId.toString());
		mFixedRequestBuilder.post(blogPath(blogName, "/post/delete"), map);
	}

	/**
	 * Reblog a given post
	 *
	 * @param blogName  the name of the blog the post is in
	 * @param postId    the id of the post
	 * @param reblogKey the reblog_key of the post
	 * @param options   Additional options (or null)
	 */
	public Post postReblog(String blogName, Long postId, String reblogKey, Map<String, ?> options) {
		if (options == null) {
			options = new HashMap<String, String>();
		}
		Map<String, Object> soptions = safeOptionMap(options);
		soptions.put("id", postId.toString());
		soptions.put("reblog_key", reblogKey);
		return mFixedRequestBuilder.post(blogPath(blogName, "/post/reblog"), soptions).getPost();
	}

	public Post postReblog(String blogName, Long postId, String reblogKey) {
		return this.postReblog(blogName, postId, reblogKey, null);
	}

	/**
	 * Save edits for a given post
	 *
	 * @param blogName The blog name of the post
	 * @param id       the Post id
	 * @param detail   The detail to save
	 */
	public void postEdit(String blogName, Long id, Map<String, ?> detail) throws IOException {
		Map<String, Object> sdetail = safeOptionMap(detail);
		sdetail.put("id", id);
		mFixedRequestBuilder.postMultipart(blogPath(blogName, "/post/edit"), sdetail);
	}

	/**
	 * Create a post
	 *
	 * @param blogName The blog name for the post
	 * @param detail   the detail to save
	 */
	public Long postCreate(String blogName, Map<String, ?> detail) throws IOException {
		return mFixedRequestBuilder.postMultipart(blogPath(blogName, "/post"), detail).getId();
	}

	/**
	 * Set up a new post of a given type
	 *
	 * @param blogName the name of the blog for this post (or null)
	 * @param klass    the type of Post to instantiate
	 * @return the new post with the client set
	 */
	public <T extends Post> T newPost(String blogName, Class<T> klass) throws IllegalAccessException, InstantiationException {
		T post = klass.newInstance();
		post.setClient(this);
		post.setBlogName(blogName);
		return post;
	}

	/**
	 * *
	 * *
	 */

	private static String blogPath(String blogName, String extPath) {
		return "/blog/" + blogUrl(blogName) + extPath;
	}

	private static String blogUrl(String blogName) {
		return blogName.contains(".") ? blogName : blogName + ".tumblr.com";
	}

	private static Map<String, Object> safeOptionMap(Map<String, ?> map) {
		Map<String, Object> mod = new HashMap<String, Object>();
		mod.putAll(map);
		return mod;
	}
}

package com.hochan.tumlodr.model.sharedpreferences;

import android.content.Context;
import android.content.SharedPreferences;

import com.hochan.tumlodr.TumlodrApp;
import com.hochan.tumlodr.model.BaseObserver;
import com.hochan.tumlodr.model.TumlodrService;
import com.hochan.tumlodr.tools.AppConfig;
import com.hochan.tumlodr.util.Events;
import com.hochan.tumlodr.util.RxBus;
import com.tumblr.jumblr.types.Blog;
import com.tumblr.jumblr.types.User;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * .
 * Created by hochan on 2017/12/9.
 */
public class UserInfo {

	public static int sLikeCount;
	public static int sFollowingCount;
	public static String sBlogName;
	public static String sUserName;

	public static void initUserInfo() {
		SharedPreferences spUserInfo = TumlodrApp.getContext().getSharedPreferences(AppConfig.SHARE_USER_INFO, Context.MODE_PRIVATE);
		sLikeCount = spUserInfo.getInt(AppConfig.SHARE_USER_INFO_LIKE_COUNT, 0);
		sFollowingCount = spUserInfo.getInt(AppConfig.SHARE_USER_INFO_FOLLOWING_COUNT, 0);
		Set<String> blogNames = spUserInfo.getStringSet(AppConfig.SHARE_USER_INFO_BLOG, null);
		if (blogNames != null && blogNames.iterator().hasNext()) {
			sBlogName = blogNames.iterator().next();
		}
		sUserName = spUserInfo.getString(AppConfig.SHARE_USER_NAME, null);
		TumlodrService.getCurrUserFromServer().subscribe(new BaseObserver<User>() {
			@Override
			public void onNext(User user) {
				saveUserInfo(user);
				sLikeCount = user.getLikeCount();
				sFollowingCount = user.getFollowingCount();
				sBlogName = user.getBlogs() != null && user.getBlogs().size() > 0 ? user.getBlogs().get(0).getName() : "";
				sUserName = user.getName();
				RxBus.getInstance().send(new Events<>(Events.EVENT_USER_INFO_UPDATE, user));
			}
		});
	}

	private static void saveUserInfo(User user) {
		SharedPreferences spTheme = TumlodrApp.mContext.getSharedPreferences(AppConfig.SHARE_USER_INFO, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = spTheme.edit();
		List<Blog> blogs = user.getBlogs();
		Set<String> blogSet = new HashSet<>();
		for (Blog blog : blogs) {
			blogSet.add(blog.getName());
		}
		editor.putStringSet(AppConfig.SHARE_USER_INFO_BLOG, blogSet);
		editor.putInt(AppConfig.SHARE_USER_INFO_LIKE_COUNT, user.getLikeCount());
		editor.putInt(AppConfig.SHARE_USER_INFO_FOLLOWING_COUNT, user.getFollowingCount());
		editor.putString(AppConfig.SHARE_USER_NAME, user.getName());
		editor.apply();
	}
}

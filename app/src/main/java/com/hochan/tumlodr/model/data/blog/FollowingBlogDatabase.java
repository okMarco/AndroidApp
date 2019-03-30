package com.hochan.tumlodr.model.data.blog;

import android.arch.paging.DataSource;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;

import com.hochan.tumlodr.TumlodrApp;
import com.hochan.tumlodr.jumblr.types.Blog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@SuppressWarnings("ALL")
@Database(entities = {FollowingBlog.class}, version = 1, exportSchema = false)
public abstract class FollowingBlogDatabase extends RoomDatabase {

    private static final String DB_NAME = "following_tumblr_blogs.db";

    private static FollowingBlogDatabase getDatabase() {
        return FollowingBlogDatabase.HOLDER.INSTANCE;
    }

    private static final class HOLDER {
        private static final FollowingBlogDatabase INSTANCE = Room.databaseBuilder(TumlodrApp.mContext,
                FollowingBlogDatabase.class, DB_NAME).build();
    }

    public abstract FollowingBlogDao getFollowingBlogDao();

    public static DataSource.Factory<Integer, FollowingBlog> getFollowingBlogs() {
        return getDatabase().getFollowingBlogDao().getFollowingBlogs();
    }

    public static void insertBlogs(final List<Blog> blogs) {
        Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter e) throws Exception {
                List<FollowingBlog> followingBlogList = new ArrayList<>();
                for (int i = 0; i < blogs.size(); i++) {
                    FollowingBlog followingBlog = FollowingBlog.parseFromBlog(blogs.get(i));
                    followingBlog.setLastVisit(0);
                    followingBlogList.add(followingBlog);
                }
                getDatabase().getFollowingBlogDao().insertFollowingBlogs(followingBlogList);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe();
    }

    public static void updateFollowingBlog(final String blogName) {
        Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter e) throws Exception {
                FollowingBlog followingBlog = getDatabase().getFollowingBlogDao().getFollowingBlogByName(blogName);
                followingBlog.setLastVisit(System.currentTimeMillis());
                getDatabase().getFollowingBlogDao().updateFollowingBlog(followingBlog);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe();
    }

    public static void insertBlog(final String name) {
        Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter e) throws Exception {
                FollowingBlog followingBlog = new FollowingBlog();
                followingBlog.setName(name);
                followingBlog.setLastVisit(System.currentTimeMillis());
                getDatabase().getFollowingBlogDao().insertFollowingBlogs(Collections.singletonList(followingBlog));
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe();
    }

    public static void deleteBlog(final String name) {
        Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter e) throws Exception {
                FollowingBlog followingBlog = getDatabase().getFollowingBlogDao().getFollowingBlogByName(name);
                getDatabase().getFollowingBlogDao().deleteFollowingBlog(followingBlog);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe();
    }

}

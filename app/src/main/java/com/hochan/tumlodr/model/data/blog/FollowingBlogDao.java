package com.hochan.tumlodr.model.data.blog;

import android.arch.paging.DataSource;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface FollowingBlogDao {

    @Query("SELECT * FROM following_tumblr_blogs order by last_visit DESC")
    DataSource.Factory<Integer, FollowingBlog> getFollowingBlogs();

    @Query("SELECT * FROM following_tumblr_blogs where name = (:name)")
    FollowingBlog getFollowingBlogByName(String name);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFollowingBlogs(List<FollowingBlog> followingBlogs);

    @Update
    void updateFollowingBlog(FollowingBlog followingBlog);

    @Delete
    void deleteFollowingBlog(FollowingBlog followingBlog);
}

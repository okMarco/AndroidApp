package com.hochan.tumlodr.model.data.blog;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import com.hochan.tumlodr.jumblr.types.Blog;

@SuppressWarnings("ALL")
@Entity(tableName = "following_tumblr_blogs", indices = {@Index(value = {"name"}, unique = true)})
public class FollowingBlog {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    @ColumnInfo(name = "last_visit")
    private long lastVisit;
    private String title;
    private String description;
    private int posts;
    private int likes;
    private int followers;
    private long updated;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getLastVisit() {
        return lastVisit;
    }

    public void setLastVisit(long lastVisit) {
        this.lastVisit = lastVisit;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPosts() {
        return posts;
    }

    public void setPosts(int posts) {
        this.posts = posts;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getFollowers() {
        return followers;
    }

    public void setFollowers(int followers) {
        this.followers = followers;
    }

    public long getUpdated() {
        return updated;
    }

    public void setUpdated(long updated) {
        this.updated = updated;
    }

    public static FollowingBlog parseFromBlog(Blog blog) {
        FollowingBlog followingBlog = new FollowingBlog();
        followingBlog.name = blog.getName();
        followingBlog.lastVisit = System.currentTimeMillis();
        followingBlog.title = blog.getTitle();
        followingBlog.description = blog.getDescription();
        followingBlog.posts = blog.getPostCount();
        followingBlog.likes = blog.getLikeCount();
        followingBlog.followers = blog.getFollowersCount();
        followingBlog.updated = blog.getUpdated();
        return followingBlog;
    }
}

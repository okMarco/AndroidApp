package com.hochan.tumlodr.model.data;

import com.tumblr.jumblr.types.VideoPost;

/**
 *
 * Created by hochan on 2016/8/25.
 */
public class TumloadrVideoPost extends VideoPost{

    private boolean mBeParse;
    private String mVideoName;
    private String mStoragePath;
    private String mVideoUrl;
    private boolean mCanDownload;
    private VideoPost mVideoPost;

    TumloadrVideoPost(VideoPost videoPost){
        this.mVideoPost = videoPost;
    }

    public boolean ismBeParse() {
        return mBeParse;
    }

    public void setmBeParse(boolean mBeParse) {
        this.mBeParse = mBeParse;
    }

    public String getmVideoName() {
        return mVideoName;
    }

    public void setmVideoName(String mVideoName) {
        this.mVideoName = mVideoName;
    }

    public String getmStoragePath() {
        return mStoragePath;
    }

    public void setmStoragePath(String mStoragePath) {
        this.mStoragePath = mStoragePath;
    }

    public String getmVideoUrl() {
        return mVideoUrl;
    }

    public void setmVideoUrl(String mVideoUrl) {
        this.mVideoUrl = mVideoUrl;
    }

    public boolean ismCanDownload() {
        return mCanDownload;
    }

    public void setmCanDownload(boolean mCanDownload) {
        this.mCanDownload = mCanDownload;
    }

    public VideoPost getmVideoPost() {
        return mVideoPost;
    }

    public void setmVideoPost(VideoPost mVideoPost) {
        this.mVideoPost = mVideoPost;
    }
}

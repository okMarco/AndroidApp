package com.hochan.tumlodr.ui.activity;

import com.hochan.tumlodr.R;
import com.hochan.tumlodr.databinding.ActivityTumblrSaveAllBinding;
import com.hochan.tumlodr.jumblr.types.Blog;
import com.hochan.tumlodr.jumblr.types.Post;
import com.hochan.tumlodr.model.BaseObserver;
import com.hochan.tumlodr.ui.activity.baseactivity.BaseViewBindingActivity;
import com.hochan.tumlodr.util.FileDownloadUtil;

import java.util.List;
import java.util.Locale;

import static com.hochan.tumlodr.model.TumlodrService.REQUEST_OFFSET;
import static com.hochan.tumlodr.model.TumlodrService.getBlogInfo;
import static com.hochan.tumlodr.model.TumlodrService.loadBlogPosts;

public class TumblrSaveAllActivity extends BaseViewBindingActivity<ActivityTumblrSaveAllBinding> {

    public static final String EXTRA_ITUMBLR_SAVE_ALL_BLOG_NAME = "blog_name";

    private boolean isFirstEnter = true;
    private String blogName;
    private int offset;
    private int totalCount;

    @Override
    public int getLayoutResourceId() {
        return R.layout.activity_tumblr_save_all;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isFirstEnter) {
            isFirstEnter = false;
            getBlogInfo(blogName).subscribe(new BaseObserver<Blog>() {
                @Override
                public void onNext(Blog blog) {
                    totalCount = blog.getPostCount();
                    getBlogPostList();
                }
            });
        }
    }

    private void getBlogPostList() {
        loadBlogPosts(blogName, REQUEST_OFFSET, offset, null).subscribe(new BaseObserver<List<Post>>() {
            @Override
            public void onNext(List<Post> posts) {
                offset += posts.size();
                viewBinding.btnImageUrlCount.setText(String.format(Locale.US, "%s %d/%d",
                        getString(R.string.ins_parse_count), offset, totalCount));
                for (Post post : posts) {
                    FileDownloadUtil.download(post);
                }
                if (offset - posts.size() < totalCount && posts.size() > 0) {

                }
            }
        });
    }

}

package com.hochan.tumlodr.ui.activity.baseactivity;

import android.os.Handler;
import android.text.TextUtils;

import com.hochan.tumlodr.module.glide.TumlodrGlide;
import com.hochan.tumlodr.module.glide.OkHoGlideUtil;
import com.hochan.tumlodr.tools.InstagramParse;
import com.hochan.tumlodr.ui.fragment.InstagramParseResultFragment;
import com.hochan.tumlodr.util.IMMLeaks;
import com.hochan.tumlodr.util.SystemUtils;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

/**
 * .
 * Created by hochan on 2018/7/29.
 */

public abstract class BaseActivity extends RxAppCompatActivity {

	@Override
	protected void onResume() {
		super.onResume();
		parseClipBoard();
		TumlodrGlide.with(this).resumeRequests();
	}

	public void parseClipBoard() {
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				String url = InstagramParse.parseClipboard(BaseActivity.this);
				if (!TextUtils.isEmpty(url)) {
					InstagramParseResultFragment fragment = new InstagramParseResultFragment();
					fragment.setUrl(url);
					fragment.show(getSupportFragmentManager(), null);
				}
			}
		}, 500);
	}

	@Override
	protected void onPause() {
		super.onPause();
		TumlodrGlide.with(this).pauseRequests();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		IMMLeaks.fixFocusedViewLeak(getApplication());
		SystemUtils.fixInputMethodManagerLeak(this);
		SystemUtils.fixInputMethod(this);
		if (OkHoGlideUtil.isContextValid(this)) {
			TumlodrGlide.with(this).onDestroy();
		}
	}
}

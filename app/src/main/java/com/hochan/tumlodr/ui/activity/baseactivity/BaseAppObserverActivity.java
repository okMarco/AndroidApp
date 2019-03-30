package com.hochan.tumlodr.ui.activity.baseactivity;

import android.annotation.TargetApi;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.WindowInsets;

import com.crashlytics.android.Crashlytics;
import com.hochan.tumlodr.R;
import com.hochan.tumlodr.tools.AppConfig;
import com.hochan.tumlodr.ui.activity.InstagramParseActivity;
import com.hochan.tumlodr.ui.component.SingleMediaScanner;
import com.hochan.tumlodr.util.ActivityLifecycleProvider;
import com.hochan.tumlodr.util.Events;
import com.hochan.tumlodr.util.RxBus;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.io.File;
import java.io.IOException;

import io.reactivex.functions.Consumer;

/**
 * .
 * Created by hochan on 2018/7/29.
 */

public abstract class BaseAppObserverActivity extends BaseActivity {

	private long mLastSnackTime;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onResume() {
		super.onResume();
		setUpObserver();
	}

	public void showCustomSnackBar(String text, int backgroundColor, int textColor, int duration,
	                               String actionName, View.OnClickListener onClickListener) {
		if (System.currentTimeMillis() - mLastSnackTime < 2000) {
			return;
		}
		mLastSnackTime = System.currentTimeMillis();
		final Snackbar snackBar = Snackbar.make(findViewById(android.R.id.content), text, Snackbar.LENGTH_SHORT);
		final Snackbar.SnackbarLayout snackLayout = (Snackbar.SnackbarLayout) snackBar.getView();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
			snackLayout.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
				@TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
				@Override
				public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
					snackLayout.setPadding(snackLayout.getPaddingLeft(),
							0,
							snackLayout.getPaddingRight(),
							insets.getSystemWindowInsetBottom());
					return insets;
				}
			});
			snackLayout.requestApplyInsets();
		}

		snackBar.setText(text);
		snackLayout.setBackgroundColor(backgroundColor);
		snackBar.setDuration(duration);
		snackBar.setActionTextColor(textColor);
		snackBar.setAction(actionName, onClickListener);
		snackLayout.bringToFront();
		snackBar.show();
	}

	public void showSimpleGreenSnackBar(String text) {
		showCustomSnackBar(text,
				ContextCompat.getColor(this, R.color.colorGreen),
				ContextCompat.getColor(this, R.color.colorWhite),
				Snackbar.LENGTH_SHORT,
				null,
				null);
	}

	public void showSimpleRedSnackBar(String text) {
		showCustomSnackBar(text,
				ContextCompat.getColor(this, R.color.colorRed),
				ContextCompat.getColor(this, R.color.colorWhite),
				Snackbar.LENGTH_SHORT,
				null,
				null);
	}

	public void showSimplePurpleSnackBar(String text) {
		showCustomSnackBar(text,
				ContextCompat.getColor(this, R.color.colorPuple),
				ContextCompat.getColor(this, R.color.colorWhite),
				Snackbar.LENGTH_SHORT,
				null,
				null);
	}

	public void showActionRedSnackBar(String text, String actionName, View.OnClickListener onClickListener) {
		showCustomSnackBar(text,
				ContextCompat.getColor(this, R.color.colorRed),
				ContextCompat.getColor(this, R.color.colorWhite),
				Snackbar.LENGTH_LONG,
				actionName,
				onClickListener);
	}

	/**
	 * 监听基本事件
	 */
	public void setUpObserver() {
		RxBus.with(new ActivityLifecycleProvider(this))
				.setEndEvent(ActivityEvent.PAUSE)
				.onNext(new Consumer<Object>() {
					@Override
					public void accept(Object o) throws Exception {
						if (o instanceof Events) {
							Events events = (Events) o;
							handleBaseObservedEvent(events);
						}
					}
				}).create();
	}

	private void handleBaseObservedEvent(Events events) {
		switch (events.mCode) {
			case Events.EVENT_CODE_DOWNLOAD_FINISH: {
				handleDownloadFinishEvent(events);
				break;
			}
			case Events.EVENT_CODE_REBLOG_SUCCESS: {
				showSimpleGreenSnackBar(getString(R.string.snackbar_forwarded));
				break;
			}
			case Events.EVENT_CODE_REBLOG_FAILE: {
				showSimpleRedSnackBar(getString(R.string.snackbar_forward_fail));
				break;
			}
			case Events.EVENT_FILE_ADD_TO_DOWNLOAD: {
				showFileAddedToDownloadSnackBar();
				break;
			}
			case Events.EVENT_VIDEO_ADD_TO_DOWNLOAD_FAIL: {
				showSimpleRedSnackBar(getString(R.string.snackbar_video_cannot_download));
				break;
			}
		}
	}

	private void handleDownloadFinishEvent(Events events) {
		if (events.mContent instanceof BaseDownloadTask) {
			BaseDownloadTask task = (BaseDownloadTask) events.mContent;
			if (!task.getPath().endsWith("jpg") && !task.getPath().endsWith("gif") && !task.getPath().endsWith("png")) {
				downloadVideoFinish(task);
			} else {
				downloadImageFinish(task);
			}
		} else if (events.mContent instanceof String) {
			showSimpleGreenSnackBar(getResources().getString(R.string.snackbar_save_finish) + events.mContent);
		}
	}

	public void showFileAddedToDownloadSnackBar() {
		showSimplePurpleSnackBar(getString(R.string.snackbar_add_to_download));
	}

	private void downloadVideoFinish(final BaseDownloadTask task) {
		if (new File(task.getPath()).exists()) {
            showVideoDownloadSuccessSnackBar(task.getPath());
		} else {
			@SuppressWarnings("ThrowableResultOfMethodCallIgnored")
			Throwable e = task.getErrorCause();
			if (e != null) {
				Crashlytics.logException(e);
				if (e instanceof IOException) {
					showActionRedSnackBar(getString(R.string.snackbar_download_video_failed) + e.getMessage(), null, null);
					if (e.getMessage().contains("Create parent directory failed")) {
						AppConfig.changeStoragePathToExternalStorage();
						task.setForceReDownload(true);
					}
				} else {
					showActionRedSnackBar(getString(R.string.snackbar_download_video_failed) + task.getUrl(), getString(R.string.retry), new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							task.setForceReDownload(true);
						}
					});
				}
			}
		}
	}

	private void downloadImageFinish(final BaseDownloadTask task) {
		if (new File(task.getPath()).exists()) {
			showImageDownloadSuccessSnackBar(task.getPath());
		} else {
			@SuppressWarnings("ThrowableResultOfMethodCallIgnored")
			Throwable e = task.getErrorCause();
			Crashlytics.logException(e);
			if (e instanceof IOException) {
				showActionRedSnackBar(getString(R.string.snackbar_save_pic_fail) + e.getMessage(), null, null);
				if (e.getMessage().contains("Create parent directory failed")) {
					AppConfig.changeStoragePathToExternalStorage();
					task.setForceReDownload(true);
				}
			} else {
				showActionRedSnackBar(getString(R.string.snackbar_save_pic_fail) + task.getUrl(), getString(R.string.retry), new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						task.setForceReDownload(true);
					}
				});
			}
		}
	}

	public void showImageDownloadSuccessSnackBar(String imagePath) {
	    showSimpleGreenSnackBar(getString(R.string.snackbar_save_finish) + imagePath);
        new SingleMediaScanner(this, imagePath);
    }

    public void showVideoDownloadSuccessSnackBar(String videoPath) {
        showSimpleGreenSnackBar(getString(R.string.snackbar_video_download_complete) + videoPath);
        new SingleMediaScanner(this, videoPath );
    }
}

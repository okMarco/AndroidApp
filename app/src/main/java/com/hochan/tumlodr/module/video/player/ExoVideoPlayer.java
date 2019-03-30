package com.hochan.tumlodr.module.video.player;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Surface;

import com.crashlytics.android.Crashlytics;
import com.devbrackets.android.exomedia.BuildConfig;
import com.devbrackets.android.exomedia.ExoMedia;
import com.devbrackets.android.exomedia.core.exoplayer.ExoMediaPlayer;
import com.devbrackets.android.exomedia.core.listener.ExoPlayerListener;
import com.devbrackets.android.exomedia.core.listener.MetadataListener;
import com.devbrackets.android.exomedia.listener.OnBufferUpdateListener;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSourceFactory;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.FileDataSource;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.upstream.cache.CacheDataSink;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.hochan.tumlodr.TumlodrApp;
import com.hochan.tumlodr.module.video.videolayout.VideoPlayLayout;
import com.hochan.tumlodr.util.FileDownloadUtil;

import java.io.File;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

import static com.google.android.exoplayer2.Player.REPEAT_MODE_ONE;

/**
 * .
 * <p>
 * Created by hochan on 2018/6/26.
 */

public class ExoVideoPlayer extends VideoPlayer implements ExoPlayerListener {

	private static final String USER_AGENT_FORMAT = "ExoMedia %s (%d) / Android %s / %s";

	private static final String USER_AGENT = String.format(Locale.US, USER_AGENT_FORMAT,
			BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE, Build.VERSION.RELEASE, Build.MODEL);

	public static ExoVideoPlayer getInstance() {
		return ExoVideoPlayer.Holder.INSTANCE;
	}

	private static final class Holder {
		static {
			ExoMedia.setDataSourceFactoryProvider(new ExoMedia.DataSourceFactoryProvider() {
				@NonNull
				@Override
				public DataSource.Factory provide(@NonNull String userAgent, @Nullable TransferListener<? super DataSource> listener) {
					return new ExoCacheDataSourceFactory();
				}
			});
		}

		private static final ExoVideoPlayer INSTANCE = new ExoVideoPlayer();
	}

	private ExoMediaPlayer mExoMediaPlayer;
	private ExoPlayer mPlayer;

	@Override
	protected void playUrl(final VideoPlayLayout videoPlayLayout) {
		createMediaPlayerIfNecessary(videoPlayLayout.getContext());
		try {
			mExoMediaPlayer.stop();
			mPlayingUrl = videoPlayLayout.getVideoUrl();
			if (TextUtils.isEmpty(mPlayingUrl)) {
				return;
			}
			if (new File(mPlayingUrl).exists()) {
				mExoMediaPlayer.setUri(Uri.parse(mPlayingUrl));
			} else {
				String videoPath = FileDownloadUtil.getVideoPathByVideoUrl(mPlayingUrl);
				if (new File(videoPath).exists()) {
					mExoMediaPlayer.setUri(Uri.parse(videoPath));
				} else {
					mExoMediaPlayer.setUri(Uri.parse(TumlodrApp.getProxy().getProxyUrl(mPlayingUrl)));
				}
			}
			mExoMediaPlayer.setPlaybackSpeed(1.0f);
			mExoMediaPlayer.setPlayWhenReady(true);
			mPlayLayoutWeakReference = new WeakReference<>(videoPlayLayout);
			videoPlayLayout.attachToVideoPlayer();
			mCurrentState = STATE_PREPARING;
			notifyPlayStateChange();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void createMediaPlayerIfNecessary(Context context) {
		if (mExoMediaPlayer == null) {
			mExoMediaPlayer = new ExoMediaPlayer(context.getApplicationContext());
		}
		mExoMediaPlayer.setRepeatMode(REPEAT_MODE_ONE);
		mExoMediaPlayer.setMetadataListener(new MetadataListener() {
			@Override
			public void onMetadata(Metadata metadata) {}
		});
		mExoMediaPlayer.setBufferUpdateListener(new OnBufferUpdateListener() {
			@Override
			public void onBufferingUpdate(int percent) {
				if (mPlayLayoutWeakReference != null && mPlayLayoutWeakReference.get() != null) {
					mPlayLayoutWeakReference.get().onBufferingUpdate(percent);
				}
			}
		});
		mExoMediaPlayer.addListener(this);

		Field[] fields = ExoMediaPlayer.class.getDeclaredFields();
		for (Field field : fields) {
			try {
				field.setAccessible(true);
				if (field.getName().equals("player")) {
					mPlayer = (ExoPlayer) field.get(mExoMediaPlayer);
					break;
				}
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void start() {
		if (mExoMediaPlayer != null) {
			if (mExoMediaPlayer.getPlaybackState() == Player.STATE_ENDED) {
				mExoMediaPlayer.restart();
			}
			mExoMediaPlayer.setPlayWhenReady(true);
		}
	}

	@Override
	public void pause() {
		if (mExoMediaPlayer != null) {
			mExoMediaPlayer.setPlayWhenReady(false);
			mCurrentState = STATE_PAUSED;
			notifyPlayStateChange();
		}
	}

	@Override
	public void seekTo(int position) {
		if (mExoMediaPlayer != null) {
			mExoMediaPlayer.seekTo(position);
		}
	}

	@Override
	protected void setSurfaceToPlayer(Surface surface) {
		mExoMediaPlayer.setSurface(surface);
	}

	@Override
	public void onStateChanged(boolean playWhenReady, int playbackState) {
		if (mExoMediaPlayer == null) {
			mCurrentState = STATE_NONE;
			return;
		}
		switch (mExoMediaPlayer.getPlaybackState()) {
			case Player.STATE_READY: {
				mCurrentState = mExoMediaPlayer.getPlayWhenReady() ? STATE_PLAYING : STATE_PAUSED;
				break;
			}
			case Player.STATE_BUFFERING: {
				mCurrentState = mExoMediaPlayer.getPlayWhenReady() ? STATE_BUFFERING_PLAYING : STATE_BUFFERING_PAUSED;
				break;
			}
			case Player.STATE_ENDED: {
				mCurrentState = STATE_COMPLETED;
				break;
			}
			case Player.STATE_IDLE: {
				mCurrentState = STATE_IDLE;
				break;
			}
			default: {
				mCurrentState = STATE_NONE;
				break;
			}
		}
		notifyPlayStateChange();
	}

	@Override
	public void onError(ExoMediaPlayer exoMediaPlayer, Exception e) {
		e.printStackTrace();
		mCurrentState = STATE_ERROR;
		notifyPlayStateChange();
		Crashlytics.logException(e);
		if (mPlayLayoutWeakReference != null && mPlayLayoutWeakReference.get() != null) {
			mPlayLayoutWeakReference.get().onPlayError(e);
		}
		release();
	}

	@Override
	public void onVideoSizeChanged(int width, int height, int unAppliedRotationDegrees,
	                               float pixelWidthHeightRatio) {
		if (mPlayLayoutWeakReference != null && mPlayLayoutWeakReference.get() != null) {
			mPlayLayoutWeakReference.get().onVideoSizeChange(width, height);
		}
	}

	@Override
	public void onSeekComplete() {

	}

	@Override
	public boolean isPlaying() {
		return mExoMediaPlayer != null && mExoMediaPlayer.getPlayWhenReady();
	}

	@Override
	public int getDuration() {
		return mExoMediaPlayer != null ? (int) mExoMediaPlayer.getDuration() : 0;
	}

	@Override
	public int getCurrentPosition() {
		return mExoMediaPlayer != null ? (int) mExoMediaPlayer.getCurrentPosition() : 0;
	}

	@Override
	public int getBufferedPosition() {
		return mPlayer != null ? (int) mPlayer.getBufferedPosition() : 0;
	}

	@Override
	public int getCurrentState() {
		if (mExoMediaPlayer == null) {
			return STATE_NONE;
		}
		return mCurrentState;
	}

	@Override
	public void release() {
		if (mExoMediaPlayer != null) {
			mExoMediaPlayer.release();
			mExoMediaPlayer = null;
			mPlayer = null;
			mPlayingUrl = null;
			mCurrentPosition = 0;
			mCurrentState = STATE_NONE;
			notifyPlayStateChange();
			if (mPlayLayoutWeakReference != null) {
				mPlayLayoutWeakReference.clear();
				mPlayLayoutWeakReference = null;
			}
		}
	}

	public static class ExoCacheDataSourceFactory implements DataSource.Factory {

		public static final SimpleCache SIMPLE_CACHE = new SimpleCache(TumlodrApp.getAppCacheDir(),
				new LeastRecentlyUsedCacheEvictor(50 * 1024 * 1024));

		private final long mMaxFileSize;
		private final DataSource.Factory mDataSourceFactory;
		private final TransferListener<Object> mTransferListener;

		public ExoCacheDataSourceFactory() {
			this(10 * 1024 * 1024);
		}

		ExoCacheDataSourceFactory(long maxFileSize) {
			super();
			this.mMaxFileSize = maxFileSize;
			mTransferListener = new DefaultBandwidthMeter();
			mDataSourceFactory = new OkHttpDataSourceFactory(new OkHttpClient.Builder()
					.connectTimeout(20, TimeUnit.SECONDS)
					.readTimeout(20, TimeUnit.SECONDS)
					.build(), USER_AGENT, mTransferListener);
		}

		@Override
		public DataSource createDataSource() {
			return new CacheDataSource(SIMPLE_CACHE, mDataSourceFactory.createDataSource(),
					new FileDataSource(), new CacheDataSink(SIMPLE_CACHE, mMaxFileSize),
					CacheDataSource.FLAG_BLOCK_ON_CACHE | CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR, null);
		}
	}
}

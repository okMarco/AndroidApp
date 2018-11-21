package com.hochan.tumlodr.module.video;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;

import com.hochan.tumlodr.module.video.player.VideoPlayer;
import com.hochan.tumlodr.module.video.videolayout.VideoPlayLayout;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

/**
 * .
 * Created by hochan on 2018/1/24.
 */

public class VideoTextureView extends TextureView implements TextureView.SurfaceTextureListener {

	private final static String TAG = "VideoTextureView";

	private static final int EGL_CONTEXT_CLIENT_VERSION = 0x3098;

	private static final int[] GL_CLEAR_CONFIG_ATTRIBUTES = {
			EGL10.EGL_RED_SIZE, 8,
			EGL10.EGL_GREEN_SIZE, 8,
			EGL10.EGL_BLUE_SIZE, 8,
			EGL10.EGL_ALPHA_SIZE, 8,
			EGL10.EGL_RENDERABLE_TYPE, EGL10.EGL_WINDOW_BIT,
			EGL10.EGL_NONE, 0,
			EGL10.EGL_NONE
	};

	private static final int[] GL_CLEAR_CONTEXT_ATTRIBUTES = {
			EGL_CONTEXT_CLIENT_VERSION, 2,
			EGL10.EGL_NONE
	};

	private SurfaceTexture mSurfaceTexture;
	public boolean mAttachOnAvailable = false;

	private int mVideoWidth;
	private int mVideoHeight;
	private final RectF mParentRect = new RectF();
	private final RectF mVideoRect = new RectF();
	private final Matrix mMatrix = new Matrix();

	public VideoTextureView(Context context) {
		this(context, null);
	}

	public VideoTextureView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public VideoTextureView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		setSurfaceTextureListener(this);
	}

	@Override
	public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
		if (mSurfaceTexture == null) {
			mSurfaceTexture = surface;
		}
		boolean should = (mVideoTextureListener != null && mVideoTextureListener.shouldAttachOnAvailable());
		if (mAttachOnAvailable || should) {
			VideoPlayer.getInstance().setSurface(new Surface(mSurfaceTexture), findOnPlayInfoListener());
			mVideoTextureListener.onSurfaceAttach();
			mAttachOnAvailable = false;
		}
	}

	public void attachToVideoPlayer() {
		if (mSurfaceTexture != null) {
			VideoPlayer.getInstance().setSurface(new Surface(mSurfaceTexture), findOnPlayInfoListener());
			mVideoTextureListener.onSurfaceAttach();
		} else {
			mAttachOnAvailable = true;
		}
	}

	@Override
	public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

	}

	@Override
	public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
		if (mVideoTextureListener != null) {
			mVideoTextureListener.onSurfaceDestroyed();
		}
		mSurfaceTexture = null;
		return true;
	}

	@Override
	public void onSurfaceTextureUpdated(SurfaceTexture surface) {

	}

	public VideoPlayLayout findOnPlayInfoListener() {
		View view = (View) getParent();
		while (!(view instanceof VideoPlayLayout)) {
			if (view.getParent() != null) {
				view = (View) view.getParent();
			} else {
				return null;
			}
		}
		return (VideoPlayLayout) view;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
		int videoHeight = mVideoHeight;
		int videoWidth = mVideoWidth;
		if (widthSpecSize > 0 && heightSpecSize > 0 && videoWidth > 0 && videoHeight > 0) {
			mParentRect.set(0, 0, widthSpecSize, heightSpecSize);
			mVideoRect.set(0, 0, mVideoWidth, mVideoHeight);
			mMatrix.setRectToRect(mVideoRect, mParentRect, Matrix.ScaleToFit.CENTER);
			mMatrix.mapRect(mVideoRect);
			setMeasuredDimension((int) mVideoRect.width(), (int) mVideoRect.height());
		} else {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}
	}

	public void setVideoSize(int width, int height) {
		mVideoWidth = width;
		mVideoHeight = height;
		requestLayout();
	}

	public float getW2HRatio() {
		if (mVideoWidth > 0 && mVideoHeight > 0) {
			return (float) (mVideoWidth * 1.0 / mVideoHeight);
		} else {
			return 1;
		}
	}

	private OnVideoTextureListener mVideoTextureListener;

	public void setVideoTextureListener(OnVideoTextureListener listener) {
		mVideoTextureListener = listener;
	}

	@SuppressWarnings("unused")
	public void clearSurface() {
		if (getSurfaceTexture() == null) {
			return;
		}

		try {
			EGL10 gl10 = (EGL10) EGLContext.getEGL();
			EGLDisplay display = gl10.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
			gl10.eglInitialize(display, null);

			EGLConfig[] configs = new EGLConfig[1];
			gl10.eglChooseConfig(display, GL_CLEAR_CONFIG_ATTRIBUTES, configs, configs.length, new int[1]);
			EGLContext context = gl10.eglCreateContext(display, configs[0], EGL10.EGL_NO_CONTEXT, GL_CLEAR_CONTEXT_ATTRIBUTES);
			EGLSurface eglSurface = gl10.eglCreateWindowSurface(display, configs[0], getSurfaceTexture(), new int[]{EGL10.EGL_NONE});

			gl10.eglMakeCurrent(display, eglSurface, eglSurface, context);
			gl10.eglSwapBuffers(display, eglSurface);
			gl10.eglDestroySurface(display, eglSurface);
			gl10.eglMakeCurrent(display, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
			gl10.eglDestroyContext(display, context);

			gl10.eglTerminate(display);
		} catch (Exception e) {
			Log.e(TAG, "Error clearing surface", e);
		}
	}

	public interface OnVideoTextureListener {
		void onSurfaceDestroyed();

		void onSurfaceAttach();

		boolean shouldAttachOnAvailable();
	}
}

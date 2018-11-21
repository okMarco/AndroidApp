package com.hochan.tumlodr.ui.fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringSystem;
import com.hochan.tumlodr.R;
import com.hochan.tumlodr.databinding.LayoutTumlodrDialogBinding;
import com.hochan.tumlodr.tools.ScreenTools;

/**
 * .
 * Created by hochan on 2018/1/23.
 */

public abstract class TumloadrDialogFragment extends DialogFragment {

	public LayoutTumlodrDialogBinding mViewBinding;
	private Spring spring;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.layout_tumlodr_dialog, container, false);
		mViewBinding = LayoutTumlodrDialogBinding.bind(view);
		return view;
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		setContentView(getContentView());
		mViewBinding.btnCancle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (spring != null) {
					spring.destroy();
				}

				mViewBinding.cvRoot.setPivotX(mViewBinding.cvRoot.getMeasuredWidth());
				mViewBinding.cvRoot.setPivotY(0);
				spring = SpringSystem.create().createSpring();
				final double endValue = ScreenTools.getScreenHeight(getContext()) / 2 + mViewBinding.cvRoot.getMeasuredHeight();
				spring.addListener(new SimpleSpringListener() {
					@Override
					public void onSpringUpdate(Spring spring) {
						super.onSpringUpdate(spring);
						mViewBinding.cvRoot.setTranslationY((float) spring.getCurrentValue());
						if (mViewBinding.cvRoot.getTranslationY() > endValue) {
							spring.destroy();
							dismiss();
						}
					}
				});
				spring.setCurrentValue(0);
				spring.setEndValue(endValue);

				onCancel();
			}
		});

		mViewBinding.btnDelete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (spring != null) {
					spring.destroy();
				}

				if (!onConfirmed()) {
					return;
				}

				mViewBinding.cvRoot.setPivotX(0);
				mViewBinding.cvRoot.setPivotY(0);
				spring = SpringSystem.create().createSpring();
				final double endValue = ScreenTools.getScreenHeight(getContext()) / 2 + mViewBinding.cvRoot.getMeasuredHeight();
				spring.addListener(new SimpleSpringListener() {
					@Override
					public void onSpringUpdate(Spring spring) {
						mViewBinding.cvRoot.setTranslationY((float) spring.getCurrentValue());
						if (mViewBinding.cvRoot.getTranslationY() > endValue) {
							spring.destroy();
							dismiss();
						}
					}
				});
				spring.setCurrentValue(0);
				spring.setEndValue(endValue);
			}
		});
	}

	public abstract View getContentView();

	public void setContentView(View view) {
		if (view == null) {
			return;
		}
		mViewBinding.flContentContainer.addView(view, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT));
	}

	public void setConfirmString(int resourceId) {
		mViewBinding.btnDelete.setText(resourceId);
	}

	public void setCancleString(int resourceId) {
		mViewBinding.btnCancle.setText(resourceId);
	}

	public void setTitleString(int resourceId) {
		mViewBinding.tvTitle.setText(resourceId);
	}

	public View findViewById(int id) {
		return mViewBinding.getRoot().findViewById(id);
	}

	@SuppressWarnings("ConstantConditions")
	@Override
	public void onStart() {
		super.onStart();
		getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
		params.gravity = Gravity.CENTER;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			params.flags |= WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
		}
		// 使用ViewGroup.LayoutParams，以便Dialog 宽度充满整个屏幕
		params.width = ViewGroup.LayoutParams.MATCH_PARENT;
		params.height = ViewGroup.LayoutParams.MATCH_PARENT;
		getDialog().getWindow().setAttributes(params);

		mViewBinding.cvRoot.setPivotX(0);
		mViewBinding.cvRoot.setPivotY(0);
		spring = SpringSystem.create().createSpring();
		spring.addListener(new SimpleSpringListener() {
			@Override
			public void onSpringUpdate(Spring spring) {
				mViewBinding.cvRoot.setTranslationY((float) spring.getCurrentValue());
			}
		});
		spring.setCurrentValue(ScreenTools.getScreenHeight(getContext()) / 2 * -1.0);
		spring.setEndValue(0);
	}

	public View getRootView() {
		return mViewBinding.getRoot();
	}

	public abstract boolean onConfirmed();

	public abstract void onCancel();
}

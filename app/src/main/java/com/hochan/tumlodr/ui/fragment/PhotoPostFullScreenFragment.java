package com.hochan.tumlodr.ui.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.widget.ImageView;

import com.hochan.tumlodr.R;
import com.hochan.tumlodr.databinding.FragmentFullViewVideoListBinding;

import java.lang.ref.WeakReference;

public class PhotoPostFullScreenFragment extends DialogFragment {

    public WeakReference<ImageView> fromImageView;
    private FragmentFullViewVideoListBinding viewBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        viewBinding = FragmentFullViewVideoListBinding.inflate(inflater, container, false);
        return viewBinding.getRoot();
    }

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

        enterAnimation();
    }

    @SuppressLint("RtlHardcoded")
    private void enterAnimation() {
        ImageView imageView = fromImageView.get();
        ImageView animationImageView = new ImageView(getActivity());
        animationImageView.setImageDrawable(imageView.getDrawable());
        int[] locationInWindow = new int[2];
        imageView.getLocationOnScreen(locationInWindow);
        ViewGroup contentView = (ViewGroup) getDialog().getWindow().getDecorView();
        int imageWidth = imageView.getLayoutParams().width;
        int imageHeight = imageView.getLayoutParams().height;
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(imageWidth, imageHeight);
        layoutParams.leftMargin = locationInWindow[0];
        layoutParams.topMargin = locationInWindow[1];
        layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        contentView.addView(animationImageView, layoutParams);
    }
}

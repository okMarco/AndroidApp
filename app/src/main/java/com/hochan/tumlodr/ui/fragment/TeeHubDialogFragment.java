package com.hochan.tumlodr.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hochan.tumlodr.R;
import com.hochan.tumlodr.databinding.LayoutTeehubBinding;
import com.hochan.tumlodr.databinding.LayoutTumlodrDialogBinding;
import com.hochan.tumlodr.ui.component.BubbleLayout;

import static com.hochan.tumlodr.ui.fragment.SettingFragment.TEEHUB_URL;

public class TeeHubDialogFragment extends ColdSoupDialogFragment {

    public static final String TAG = "TeeHubDialogFragment";

    @Override
    public View getContentView() {
        viewBinding.llRootContainer.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorBackgroundBlue));
        viewBinding.tvTitle.setVisibility(View.GONE);
        viewBinding.llBottomContainer.setVisibility(View.GONE);

        LayoutTeehubBinding layoutTeehubBinding = LayoutTeehubBinding.inflate(getLayoutInflater());
        layoutTeehubBinding.btnShowTeeHub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse(TEEHUB_URL);
                intent.setData(content_url);
                startActivity(intent);
            }
        });
        layoutTeehubBinding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancle();
            }
        });
        return layoutTeehubBinding.getRoot();
    }

    @Override
    public boolean onConfirmed() {
        return false;
    }

    @Override
    public void onCancel() {
    }
}

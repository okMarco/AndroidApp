package com.hochan.tumlodr.ui.component;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hochan.tumlodr.R;

/**
 *
 * Created by hochan on 2016/8/27.
 */
@CoordinatorLayout.DefaultBehavior(TumloadrSnackBar.Behavior.class)
public class TumloadrSnackBar extends LinearLayout{

    private TextView tvMessage;
    private Button btnNeg, btnOps;

    public TumloadrSnackBar(Context context) {
        this(context, null);
    }

    public TumloadrSnackBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TumloadrSnackBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View view = LayoutInflater.from(context).inflate(R.layout.snack_bar, null, false);
        tvMessage = (TextView) view.findViewById(R.id.tv_message);
        btnNeg = (Button) view.findViewById(R.id.btn_neg);
        btnOps = (Button) view.findViewById(R.id.btn_ops);

        btnNeg.setText("取消");
        btnNeg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        addView(view, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    public void show(){
        show("", null);
    }

    public void show(String message, OnClickListener onClickListener){
        show(message, "确定", onClickListener);
    }

    public void show(String message, String positive, OnClickListener onClickListener){
        btnOps.setText(positive);
        btnOps.setOnClickListener(onClickListener);
        //animate().translationY(-getMeasuredHeight()).setDuration(200).start();
        setTranslationY(-getMeasuredHeight());
    }

    public void dismiss(){
        animate().translationY(0).setDuration(200).start();
        btnOps.setOnClickListener(null);
    }

    public static class Behavior extends CoordinatorLayout.Behavior<TumloadrSnackBar>{

        @Override
        public boolean onLayoutChild(CoordinatorLayout parent, TumloadrSnackBar child, int layoutDirection) {
            child.layout(0, parent.getMeasuredHeight(), child.getMeasuredWidth(),
                    parent.getMeasuredHeight() + child.getMeasuredHeight());
            return true;
        }
    }
}

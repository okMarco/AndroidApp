package com.hochan.tumlodr.ui.component;

import android.content.Context;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.hochan.tumlodr.tools.ScreenTools;

/**
 *
 * Created by hochan on 2016/8/27.
 */
@CoordinatorLayout.DefaultBehavior(FabButtonLayout.Behavior.class)
public class FabButtonLayout extends LinearLayout{

    public FabButtonLayout(Context context) {
        super(context);
    }

    public FabButtonLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FabButtonLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        //super.onLayout(changed, l, t, r, b);
        int widthOfFabButton = getChildAt(0).getMeasuredWidth();
        int gap = (int) ((getMeasuredWidth() - widthOfFabButton * 4) * 1.0 / 5.0);
        System.out.println(gap + "*******************************");
        if(changed){
            for(int i = 0; i < getChildCount(); i++){
                View view = getChildAt(i);
                int left = gap * (i + 1) + widthOfFabButton * i;
                int right = left + widthOfFabButton;
                int bottom = getMeasuredHeight() - ScreenTools.dip2px(getContext(), 25);
                int top = bottom - widthOfFabButton;
                view.layout(left, top, right, bottom);
                view.setTranslationY(view.getMeasuredWidth() + ScreenTools.dip2px(getContext(), 40));
            }
        }
    }

    static class Behavior extends CoordinatorLayout.Behavior{

        private static final boolean SNACKBAR_BEHAVIOR_ENABLED = Build.VERSION.SDK_INT >= 11;

        @Override
        public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
            return SNACKBAR_BEHAVIOR_ENABLED && dependency instanceof Snackbar.SnackbarLayout;
        }

        @Override
        public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
            System.out.println("onDependentViewChanged%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
            if(dependency instanceof Snackbar.SnackbarLayout){
                System.out.println(dependency.getTop());
                System.out.println(dependency.getTranslationY());
                child.setTranslationY(-dependency.getMeasuredHeight());
            }
            return false;
        }

        @Override
        public void onDependentViewRemoved(CoordinatorLayout parent, View child, View dependency) {
            if(dependency instanceof Snackbar.SnackbarLayout){
                child.animate().translationY(0).setDuration(200).start();
            }
        }
    }
}

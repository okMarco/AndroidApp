package com.hochan.tumlodr.ui.component;

import android.content.Context;
import android.graphics.Point;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 *
 * Created by hochan on 2016/5/23.
 */
public class RecordTouchCardView extends CardView{

    private int mDownX;
    private int mDownY;

    public RecordTouchCardView(Context context) {
        super(context);
    }

    public RecordTouchCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RecordTouchCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                mDownX = (int) event.getRawX();
                mDownY = (int) event.getRawY();
                break;
        }
        return super.onTouchEvent(event);
    }

    public int getmDownX() {
        return mDownX;
    }

    public int getmDownY() {
        return mDownY;
    }

    public Point getTouchPoint(){
        Point point = new Point(mDownX, mDownY);
        return point;
    }
}

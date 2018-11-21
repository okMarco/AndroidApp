package com.hochan.tumlodr.util;

import android.graphics.Color;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.view.MotionEvent;
import android.widget.TextView;

/**
 *
 * Created by hochan on 2016/9/7.
 */
public class TumlodrLinkMovementMethod extends LinkMovementMethod{

	private static TumlodrLinkMovementMethod mMovementMethod;

	public static TumlodrLinkMovementMethod getInstance(){
		if(mMovementMethod == null){
			mMovementMethod = new TumlodrLinkMovementMethod();
		}
		return mMovementMethod;
	}

	@Override
	public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
		int action = event.getAction();

		if (action == MotionEvent.ACTION_UP ||
				action == MotionEvent.ACTION_DOWN) {
			int x = (int) event.getX();
			int y = (int) event.getY();

			x -= widget.getTotalPaddingLeft();
			y -= widget.getTotalPaddingTop();

			x += widget.getScrollX();
			y += widget.getScrollY();

			Layout layout = widget.getLayout();
			int line = layout.getLineForVertical(y);
			int off = layout.getOffsetForHorizontal(line, x);

			ClickableSpan[] link = buffer.getSpans(off, off, ClickableSpan.class);

			if (link.length != 0) {
				if (action == MotionEvent.ACTION_UP) {
					link[0].onClick(widget);

					buffer.setSpan(new BackgroundColorSpan(Color.TRANSPARENT),
							buffer.getSpanStart(link[0]),
							buffer.getSpanEnd(link[0]),
							Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				} else if (action == MotionEvent.ACTION_DOWN) {

					buffer.setSpan(new BackgroundColorSpan(Color.parseColor("#20000000")),
							buffer.getSpanStart(link[0]),
							buffer.getSpanEnd(link[0]),
							Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

					Selection.setSelection(buffer,
							buffer.getSpanStart(link[0]),
							buffer.getSpanEnd(link[0]));
				} else if(action == MotionEvent.ACTION_MOVE){
					buffer.setSpan(new BackgroundColorSpan(Color.TRANSPARENT),
							buffer.getSpanStart(link[0]),
							buffer.getSpanEnd(link[0]),
							Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					Selection.removeSelection(buffer);
				}
				return false;
			} else {
				Selection.removeSelection(buffer);
			}
		}
		return false;
	}
}

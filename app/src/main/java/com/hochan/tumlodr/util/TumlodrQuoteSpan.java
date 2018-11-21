package com.hochan.tumlodr.util;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Layout;
import android.text.style.QuoteSpan;

/**
 *
 * Created by zhendong_chen on 2016/9/5.
 */
public class TumlodrQuoteSpan extends QuoteSpan{

	@Override
	public int getLeadingMargin(boolean first) {
		return 20;
	}

	@Override
	public void drawLeadingMargin(Canvas c, Paint p,
	                              int x, int dir, int top,
	                              int baseline, int bottom,
	                              CharSequence text, int start,
	                              int end, boolean first, Layout layout) {
		int color = p.getColor();
		p.setColor(Color.parseColor("#cacaca"));
		c.drawRect(x, top, x+5, bottom, p);
		p.setColor(color);
	}
}

package com.hochan.tumlodr.util;

import android.text.TextPaint;
import android.text.style.URLSpan;
import android.view.View;

import com.hochan.tumlodr.module.webbrowser.WebViewActivity;

/**
 * .
 * Created by hochan on 2016/9/7.
 */
public class TumlodrUrlSpan extends URLSpan {

	public TumlodrUrlSpan(String url) {
		super(url);
	}

	@Override
	public void updateDrawState(TextPaint ds) {
		ds.setFakeBoldText(true);
		ds.setUnderlineText(true);
		ds.setColor(ds.getColor());
	}

	@Override
	public void onClick(View widget) {
		WebViewActivity.showUrl(widget.getContext(), getURL());
	}
}

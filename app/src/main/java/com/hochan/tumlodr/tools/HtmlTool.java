package com.hochan.tumlodr.tools;

import android.support.annotation.NonNull;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.QuoteSpan;
import android.text.style.URLSpan;
import android.widget.TextView;

import com.hochan.tumlodr.module.glide.TumlodrGlide;
import com.hochan.tumlodr.util.TumlodrQuoteSpan;
import com.hochan.tumlodr.util.TumlodrUrlSpan;

/**
 * .
 * Created by zhendong_chen on 2016/9/5.
 */
public class HtmlTool {

	public static SpannableStringBuilder fromHtml(String source, @NonNull final TextView textView) {

		if (TextUtils.isEmpty(source)) {
			return new SpannableStringBuilder("");
		}

		textView.setMovementMethod(LinkMovementMethod.getInstance());

		Spanned spanned = Html.fromHtml(source);

		SpannableStringBuilder sp = (SpannableStringBuilder) spanned;
		QuoteSpan[] quoteSpans = sp.getSpans(0, sp.length(), QuoteSpan.class);
		for (QuoteSpan quoteSpan : quoteSpans) {
			int start = sp.getSpanStart(quoteSpan);
			int end = sp.getSpanEnd(quoteSpan);
			sp.removeSpan(quoteSpan);
			TumlodrQuoteSpan tumQuoteSpan = new TumlodrQuoteSpan();
			sp.setSpan(tumQuoteSpan, start, end, 0);
		}

		URLSpan[] urlSpans = sp.getSpans(0, sp.length(), URLSpan.class);
		for (URLSpan urlSpan : urlSpans) {
			int start = sp.getSpanStart(urlSpan);
			int end = sp.getSpanEnd(urlSpan);
			sp.removeSpan(urlSpan);
			TumlodrUrlSpan tumlodrUrlSpan = new TumlodrUrlSpan(urlSpan.getURL());
			sp.setSpan(tumlodrUrlSpan, start, end, 0);
		}

		return sp;
	}
}

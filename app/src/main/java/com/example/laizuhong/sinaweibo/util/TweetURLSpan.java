package com.example.laizuhong.sinaweibo.util;

import android.os.Parcel;
import android.text.TextPaint;
import android.text.style.URLSpan;

/**
 * 清除@{@link android.widget.TextView}链接中的下划线，太丑了，话说google怎么没有这个api选项呢？
 *
 * @author longkai
 */
public class TweetURLSpan extends URLSpan {

    public TweetURLSpan(String url) {
        super(url);
    }

    public TweetURLSpan(Parcel src) {
        super(src);
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setColor(ds.linkColor);
        ds.setUnderlineText(false);
    }
}

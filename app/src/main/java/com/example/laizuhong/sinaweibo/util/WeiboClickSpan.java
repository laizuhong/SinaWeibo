package com.example.laizuhong.sinaweibo.util;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

/**
 * Created by laizuhong on 2015/9/18.
 */
public class WeiboClickSpan extends ClickableSpan{


    private OnTextviewClickListener listener;

    public WeiboClickSpan(OnTextviewClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View widget) {
        this.listener.clickTextView();
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        listener.setStyle(ds);
        super.updateDrawState(ds);
    }
}

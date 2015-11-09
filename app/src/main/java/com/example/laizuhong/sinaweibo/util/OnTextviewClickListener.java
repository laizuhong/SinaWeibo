package com.example.laizuhong.sinaweibo.util;

import android.text.TextPaint;
import android.widget.TextView;

/**
 * Created by laizuhong on 2015/9/18.
 */
public interface OnTextviewClickListener {
    void clickTextView(TextView textView);

    void setStyle(TextPaint ds);
}

package com.example.laizuhong.sinaweibo.util;

import android.content.Context;
import android.content.Intent;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.widget.TextView;

import com.example.laizuhong.sinaweibo.R;
import com.example.laizuhong.sinaweibo.TopicActivity;
import com.example.laizuhong.sinaweibo.UserActivity;
import com.example.laizuhong.sinaweibo.UserWeiboActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by laizuhong on 2015/9/30.
 */
public class StringUtil {

    private static final Pattern itemuser = Pattern.compile("@[\\u4e00-\\u9fa5a-zA-Z0-9_-]+");
    private static final Pattern web1 = Pattern.compile("(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");
    private static final Pattern web2 = Pattern.compile("#[^#]+#");

    public static void setTextview(TextView textview, final Context context) {
        String string = textview.getText().toString();
        SpannableString spannableString = new SpannableString(string);
        setKeyworkClickable(textview, spannableString, itemuser, new WeiboClickSpan(new OnTextviewClickListener() {
            @Override
            public void clickTextView(TextView tv) {
                MyLog.e(tv.getText().toString());
                Intent intent = new Intent(context, UserActivity.class);
                context.startActivity(intent);
            }

            @Override
            public void setStyle(TextPaint ds) {
                ds.setColor(context.getResources().getColor(R.color.green));
                ds.setUnderlineText(false);
            }
        }));
        setKeyworkClickable(textview, spannableString, web1, new WeiboClickSpan(new OnTextviewClickListener() {
            @Override
            public void clickTextView(TextView tv) {
                Intent intent = new Intent(context, UserWeiboActivity.class);
                context.startActivity(intent);
            }

            @Override
            public void setStyle(TextPaint ds) {
                ds.setColor(context.getResources().getColor(R.color.green));
                ds.setUnderlineText(false);
            }
        }));
        setKeyworkClickable(textview, spannableString, web2, new WeiboClickSpan(new OnTextviewClickListener() {
            @Override
            public void clickTextView(TextView tv) {
                Intent intent = new Intent(context, TopicActivity.class);
                context.startActivity(intent);
            }

            @Override
            public void setStyle(TextPaint ds) {
                ds.setColor(context.getResources().getColor(R.color.green));
                ds.setUnderlineText(false);
            }
        }));
    }

    /*
     *设置具体哪个关键字可点击
     */
    private static void setKeyworkClickable(TextView textView, SpannableString spannableString, Pattern pattern, ClickableSpan clickableSpan) {
        Matcher matcher = pattern.matcher(spannableString.toString());
        while (matcher.find()) {
            String key = matcher.group();
            if (!"".equals(key)) {
                int start = spannableString.toString().indexOf(key);
                int end = start + key.length();
                setClickTextview(textView, spannableString, start, end, clickableSpan);
            }
        }
    }

    /*
     *设置textview中的字段可点击
     */
    private static void setClickTextview(TextView textview, SpannableString spannableString, int start, int end, ClickableSpan clickableSpan) {
        spannableString.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textview.setText(spannableString);
        textview.setMovementMethod(LinkMovementMethod.getInstance());
    }


    /**
     * 半角转换为全角
     *
     * @param input
     * @return
     */
    public static String ToDBC(String input) {
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 12288) {
                c[i] = (char) 32;
                continue;
            }
            if (c[i] > 65280 && c[i] < 65375)
                c[i] = (char) (c[i] - 65248);
        }
        return new String(c);
    }
}

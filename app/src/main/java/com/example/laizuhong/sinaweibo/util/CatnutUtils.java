package com.example.laizuhong.sinaweibo.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.provider.BaseColumns;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;


/**
 * Created by laizuhong on 2015/10/8.
 */
public class CatnutUtils {


    private static final String TAG = "CatnutUtils";

    /**
     * cursor默认没有得到boolean类型的值，所以才有了这个方法
     *
     * @param cursor
     * @param columnName
     * @return x == 1 ? true : false
     */
    public static boolean getBoolean(Cursor cursor, String columnName) {
        return cursor.getInt(cursor.getColumnIndex(columnName)) == 1 ? true : false;
    }

    /**
     * 判断实际的int是否为0，否则返回默认的int
     *
     * @param real
     * @param defaultValue
     * @return int
     */
    public static int optValue(int real, int defaultValue) {
        return real == 0 ? defaultValue : real;
    }

    /**
     * 判断实际的long是否为0，否则返回默认的long
     *
     * @param real
     * @param defaultValue
     * @return long
     */
    public static long optValue(long real, long defaultValue) {
        return real == 0L ? defaultValue : real;
    }

    /**
     * 判断实际的String是否为null或者空串，否则返回默认的字符串
     *
     * @param real
     * @param defaultValue
     * @return String
     */
    public static String optValue(String real, String defaultValue) {
        return TextUtils.isEmpty(real) ? defaultValue : real;
    }

    /**
     * 判断实际的float是否为0.0f，否则返回默认的float
     *
     * @param real
     * @param defaultValue
     * @return float
     */
    public static float optValue(float real, float defaultValue) {
        return real == 0.0F ? defaultValue : real;
    }

    /**
     * 判断实际的double是否为0.0D，否则返回默认的double
     *
     * @param real
     * @param defaultValue
     * @return double
     */
    public static double optValue(double real, double defaultValue) {
        return real == 0.0D ? defaultValue : real;
    }

    /**
     * 判断实际的boolean是否为false，否则返回默认的boolean
     *
     * @param real
     * @param defaultValue
     * @return boolean
     */
    public static boolean optValue(boolean real, boolean defaultValue) {
        return !real ? defaultValue : real;
    }

    /**
     * 将数字转换为相对数字，节省版面，比如1234->1.2k，34567->3.5w
     * <p/>
     * 注意，0返回null，最大的权值为w
     *
     * @param number
     * @return 数字相对大小字符串
     */
    public static String approximate(int number) {
        if (number == 0) {
            return null;
        }
        if (number < 1000) {
            return String.valueOf(number);
        } else if (number < 10000) {
            float f = number * 1f / 1000;
            return String.valueOf(Math.round(f * 10) / 10.0) + "k";
        } else {
            float f = number * 1f / 10000;
            return String.valueOf(Math.round(f * 10) / 10.0) + "w";
        }
    }

    /**
     * 四舍五入float
     *
     * @param num 浮点数
     * @param bit 多少位
     * @return the  you required
     */
    public static float scaleNumber(float num, int bit) {
        int range = 10 * bit;
        return (float) Math.round(num * range) / range;
    }

    /**
     * 设置某个view里面的textview的text
     *
     * @param parent     父容器
     * @param textViewId textview的id
     * @param text
     * @return that textview
     */
    public static TextView setText(View parent, int textViewId, CharSequence text) {
        TextView textView = (TextView) parent.findViewById(textViewId);
        textView.setText(text);
        return textView;
    }

//    /**
//     * 自定义raw sql以获取cursor，包含所有的除了having，group外的所有字段
//     *
//     * @param context
//     * @param uri
//     * @param projection
//     * @param selection
//     * @param selectionArgs
//     * @param from
//     * @param joinOn
//     * @param sort
//     * @param limit
//     * @return {@link android.content.CursorLoader}
//     */
//    public static CursorLoader getCursorLoader(Context context, Uri uri, String[] projection, String selection, String[] selectionArgs, String from, String joinOn, String sort, String limit) {
//        String sql = buildQuery(projection, selection, from, joinOn, sort, limit);
//        return new CursorLoader(context, uri, null, sql, selectionArgs, null);
//    }

    /**
     * 模仿android的凑sql的方法，不同在于支持join和on，用在raw-query
     *
     * @param projection
     * @param selection
     * @param from
     * @param joinOn     自己写上什么类型的join和on关键字(right, full not support)
     * @param sort
     * @param limit
     * @return query sql
     */
    public static String buildQuery(String[] projection, String selection, String from, String joinOn, String sort, String limit) {
        StringBuilder query = new StringBuilder("SELECT");
        if (projection == null) {
            query.append(" * ");
        } else {
            query.append(" ").append(projection(projection));
        }

        query.append(" FROM ").append(from);

        if (joinOn != null) {
            // 至于是什么类型的join，多少个join和on，这里得自己写上
            query.append(" ").append(joinOn);
        }

        if (selection != null) {
            query.append(" WHERE ").append(selection);
        }

        if (sort != null) {
            query.append(" ORDER BY ").append(sort);
        }

        if (limit != null) {
            query.append(" LIMIT ").append(limit);
        }

        return query.toString();
    }

    /**
     * 给字符串首位加上单引号
     */
    public static String quote(String string) {
        return "'" + string + "'";
    }

    /**
     * 拼接投影sql
     *
     * @param projection
     * @return turn a string[] into xx,xxx,xxx,...,xx
     */
    private static String projection(String[] projection) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < projection.length; i++) {
            sb.append(projection[i]);
            if (i != projection.length - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }


    /**
     * 对某个字段自增或者自减法，只能通过_id=?来唯一确定哦
     *
     * @param increment
     * @param table
     * @param column
     * @param _id
     * @return update sql
     */
    public static String increment(boolean increment, String table, String column, long _id) {
        return new StringBuilder("UPDATE ")
                .append(table).append(" ").append(" SET ")
                .append(column).append("=").append(column)
                .append(increment ? "+1" : "-1")
                .append(" WHERE ").append(BaseColumns._ID)
                .append("=").append(_id)
                .toString();
    }

    private static String reflectString(Object object) {
        if (object instanceof CharSequence) {
            return quote(object.toString());
        } else {
            // 其它情况下一律认为是number类型
            return object.toString();
        }
    }

    /**
     * simple wrap the keywords with '%xx%'
     *
     * @param keywords
     * @return '%keywords%'
     */
    public static String like(String keywords) {
        return "'%" + keywords + "%'";
    }

    /**
     * 给微博文字加上样式，表情，##，@，链接等
     *
     * @param text      微博
     * @param imageSpan 不需要表情传入null
     */
    public static void vividTweet(TweetTextView text, TweetImageSpan imageSpan) {
        if (imageSpan != null) {
            text.setText(imageSpan.getImageSpan(text.getText()));
        }
        Linkify.addLinks(text, TweetTextView.MENTION_PATTERN, TweetTextView.MENTION_SCHEME, null, TweetTextView.MENTION_FILTER);
        Linkify.addLinks(text, TweetTextView.TOPIC_PATTERN, TweetTextView.TOPIC_SCHEME, null, TweetTextView.TOPIC_FILTER);
        Linkify.addLinks(text, TweetTextView.WEB_URL, null, null, TweetTextView.URL_FILTER);
        removeLinkUnderline(text);
    }

    /**
     * 清除textview链接的下划线
     *
     * @param textView
     */
    public static void removeLinkUnderline(TextView textView) {
        Spannable s = Spannable.Factory.getInstance().newSpannable(textView.getText());
        URLSpan[] spans = s.getSpans(0, s.length(), URLSpan.class);
        for (URLSpan span : spans) {
            int start = s.getSpanStart(span);
            int end = s.getSpanEnd(span);
            s.removeSpan(span);
            span = new TweetURLSpan(span.getURL());
            s.setSpan(span, start, end, 0);
        }
        textView.setText(s);
    }

    /**
     * 当使用list pref时只能存储string array，so 这个方法你懂的
     *
     * @param pref
     * @param key
     * @param defaultValue
     * @return key exists ? existed value : defaultValue
     */
    public static int resolveListPrefInt(SharedPreferences pref, String key, int defaultValue) {
        String value = pref.getString(key, null);
        if (value == null) {
            return defaultValue;
        }
        return Integer.parseInt(value);
    }


    /**
     * 微博文字转表情
     */
    public static SpannableString text2Emotion(Context context, String key) {
        SpannableString spannable = new SpannableString(key);
        InputStream inputStream = null;
        Drawable drawable = null;
        try {
            inputStream = context.getAssets().open(TweetImageSpan.EMOTIONS_DIR + TweetImageSpan.EMOTIONS.get(key));
            drawable = Drawable.createFromStream(inputStream, null);
        } catch (IOException e) {
            Log.e(TAG, "load emotion error!", e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    Log.e(TAG, "close input error!", e);
                }
            }
        }
        if (drawable != null) {
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
            spannable.setSpan(span, 0, key.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        return spannable;
    }


}

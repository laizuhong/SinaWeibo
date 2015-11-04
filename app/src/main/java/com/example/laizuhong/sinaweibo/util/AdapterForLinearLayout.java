package com.example.laizuhong.sinaweibo.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.laizuhong.sinaweibo.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.sina.weibo.sdk.openapi.models.Comment;

import java.util.List;

/**
 * Created by laizuhong on 2015/11/3.
 */
public class AdapterForLinearLayout extends BaseAdapter {


    Context context;
    TweetImageSpan tweetImageSpan;
    DisplayImageOptions options;
    private List<Comment> data;


    public AdapterForLinearLayout(List<Comment> data, Context context) {
        this.data = data;
        this.context = context;
        tweetImageSpan = new TweetImageSpan(context);
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .displayer(new FadeInBitmapDisplayer(500)) // 展现方式：渐现
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }


    public int getCount() {

        return data.size();

    }


    public Object getItem(int position) {

        return data.get(position);

    }


    public long getItemId(int position) {

        return position;

    }


//亮点在这里，getView方法呈现

    @SuppressWarnings("unchecked")

    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = LinearLayout.inflate(context, R.layout.item_comment, null);

        Comment item = data.get(position);


        TextView name = (TextView) convertView.findViewById(R.id.username);
        TextView time = (TextView) convertView.findViewById(R.id.time);
//      TextView count = (TextView) convertView.findViewById(R.id.comment_like_count);
        TweetTextView text = (TweetTextView) convertView.findViewById(R.id.comment_text);
        ImageView head = (ImageView) convertView.findViewById(R.id.userhead);


        name.setText(item.user.screen_name);
        time.setText(DateUtil.GmtToDatastring(item.created_at).substring(5, 16));

        text.setText(item.text);
        CatnutUtils.vividTweet(text, tweetImageSpan);

        ImageLoader.getInstance().displayImage(item.user.profile_image_url, head, options);
        //bindView(v, item, from);


        convertView.setTag(position);//一定要注意这个东西，这个可是我们判断哪一个子项被点击的重要依据，你可以往里面set任何对象的

        return convertView;

    }

//接下来是bindview，我在这里面实现了imageview的功能，simpleAdapter没有的东西哟

//    @SuppressWarnings("unchecked")
//
//    private void bindView(View v, Comment item, String from) {
//
//
//        Object data = item.get(from);
//
//
//        if (v instanceof Checkable) {
//
//            if (data instanceof Boolean) {
//
//                ((Checkable) v).setChecked((Boolean) data);
//
//            } else {
//
//                throw new IllegalStateException(v.getClass().getName()
//
//                        + " should be bound to a Boolean, not a "
//
//                        + data.getClass());
//
//            }
//
//        } else if (v instanceof TextView) {
//
//            ((TextView) v).setText(data == null ? "" : data.toString());
//
//        } else if (v instanceof ImageView) {
//
//            if (data != null && !data.equals("")) {
//
//                if (data instanceof Integer) {
//
//                    ((ImageView) v).setImageResource((Integer) data);
//
//                } else {
//
//                    Bitmap bm = ImageCache.getInstance().get(data.toString(),//这方法是自己写的
//
//                            (ImageView) v);
//
//                    ((ImageView) v).setImageBitmap(bm);
//
//
//                    ((ImageView) v).setPadding(2, 2, 2, 2);
//
//                }
//
//            }
//
//
//        } else {
//
//            throw new IllegalStateException(v.getClass().getName()
//
//                    + " is not a "
//
//                    + " view that can be bounds by this SimpleAdapter");
//
//        }
//
//    }
}

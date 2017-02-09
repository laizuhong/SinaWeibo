package com.example.laizuhong.sinaweibo.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.laizuhong.sinaweibo.MyApp;
import com.example.laizuhong.sinaweibo.R;
import com.example.laizuhong.sinaweibo.UserActivity;
import com.example.laizuhong.sinaweibo.util.CatnutUtils;
import com.example.laizuhong.sinaweibo.util.DateUtil;
import com.example.laizuhong.sinaweibo.util.MyLog;
import com.example.laizuhong.sinaweibo.util.TweetImageSpan;
import com.example.laizuhong.sinaweibo.util.TweetTextView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.sina.weibo.sdk.openapi.models.Comment;

import java.util.List;

/**
 * Created by laizuhong on 2015/9/30.
 */
public class CommentAdapter extends BaseAdapter {

    List<Comment> comments;
    Context context;
//    DisplayImageOptions options;
    TweetImageSpan tweetImageSpan;

    public CommentAdapter(List<Comment> comments, Context context) {
        this.comments = comments;
        this.context = context;
        tweetImageSpan = new TweetImageSpan(context);
//        options = new DisplayImageOptions.Builder()
//                .cacheInMemory(true)
//                .cacheOnDisk(true)
//                .displayer(new FadeInBitmapDisplayer(500)) // 展现方式：渐现
//                .considerExifParams(true)
//                .bitmapConfig(Bitmap.Config.RGB_565)
//                .build();
    }

    @Override
    public int getCount() {
        return comments.size();
    }

    @Override
    public Object getItem(int position) {
        return comments.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holer holer = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_comment, null);
            holer = new Holer();
            holer.name = (TextView) convertView.findViewById(R.id.username);
            holer.time = (TextView) convertView.findViewById(R.id.time);
            holer.count = (TextView) convertView.findViewById(R.id.comment_like_count);
            holer.text = (TweetTextView) convertView.findViewById(R.id.comment_text);
            holer.head = (ImageView) convertView.findViewById(R.id.userhead);
            convertView.setTag(holer);
        } else {
            holer = (Holer) convertView.getTag();
        }

        MyLog.e(position + "");
        Comment comment = comments.get(position);
        holer.name.setText(comment.user.screen_name);
        holer.head.setTag(position);
        ImageLoader.getInstance().displayImage(comment.user.profile_image_url, holer.head, MyApp.options, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {

                ImageView imageView = (ImageView) view;
                imageView.setImageResource(R.drawable.ic_logo);
            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                ImageView imageView = (ImageView) view;
                imageView.setImageBitmap(bitmap);
            }

            @Override
            public void onLoadingCancelled(String s, View view) {

            }
        }, new ImageLoadingProgressListener() {
            @Override
            public void onProgressUpdate(String s, View view, int i, int i1) {

            }
        });

        holer.head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int p = (int) v.getTag();
                String name = comments.get(p).user.screen_name;
                Intent intent = new Intent(context, UserActivity.class);
                intent.putExtra("name", name);
                context.startActivity(intent);
            }
        });
        //ImageLoader.getInstance().displayImage(comment.user.profile_image_url, holer.head, options);
        holer.time.setText(DateUtil.GmtToDatastring(comment.created_at).substring(5, 16));
        holer.text.setText(comment.text);
        CatnutUtils.vividTweet(holer.text, tweetImageSpan);

        return convertView;
    }

    class Holer {
        TweetTextView text;
        TextView name, time, count;
        ImageView head;
    }

}

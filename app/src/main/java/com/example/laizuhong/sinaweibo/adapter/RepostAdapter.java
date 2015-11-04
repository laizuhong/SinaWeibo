package com.example.laizuhong.sinaweibo.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.laizuhong.sinaweibo.R;
import com.example.laizuhong.sinaweibo.bean.Repost;
import com.example.laizuhong.sinaweibo.util.CatnutUtils;
import com.example.laizuhong.sinaweibo.util.DateUtil;
import com.example.laizuhong.sinaweibo.util.MyLog;
import com.example.laizuhong.sinaweibo.util.TweetImageSpan;
import com.example.laizuhong.sinaweibo.util.TweetTextView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.util.List;

/**
 * Created by laizuhong on 2015/11/4.
 */
public class RepostAdapter extends BaseAdapter {

    Context context;
    List<Repost> list;
    DisplayImageOptions options;
    TweetImageSpan tweetImageSpan;

    public RepostAdapter(Context context, List<Repost> list) {
        this.context = context;
        this.list = list;
        tweetImageSpan = new TweetImageSpan(context);
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .displayer(new FadeInBitmapDisplayer(500)) // 展现方式：渐现
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
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
            holer.repost = (TextView) convertView.findViewById(R.id.repost);
            convertView.setTag(holer);
        } else {
            holer = (Holer) convertView.getTag();
        }

        MyLog.e(position + "");
        Repost item = list.get(position);
        holer.name.setText(item.getUser().screen_name);
        ImageLoader.getInstance().displayImage(item.getUser().profile_image_url, holer.head, options);
        holer.time.setText(DateUtil.GmtToDatastring(item.getCreated_at()).substring(5, 16));
        holer.text.setText(item.getText());
        if (item.getReposts_count() != 0) {
            holer.repost.setVisibility(View.VISIBLE);
            holer.repost.setText("转发" + item.getReposts_count());
        } else {
            holer.repost.setVisibility(View.GONE);
        }
        CatnutUtils.vividTweet(holer.text, tweetImageSpan);

        return convertView;
    }

    class Holer {
        TweetTextView text;
        TextView name, time, count, repost;
        ImageView head;
    }
}

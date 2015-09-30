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
import com.example.laizuhong.sinaweibo.util.DateUtil;
import com.example.laizuhong.sinaweibo.util.StringUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.sina.weibo.sdk.openapi.models.Comment;

import java.util.List;

/**
 * Created by laizuhong on 2015/9/30.
 */
public class CommentAdapter extends BaseAdapter {

    List<Comment> comments;
    Context context;
    DisplayImageOptions options;

    public CommentAdapter(List<Comment> comments, Context context) {
        this.comments = comments;
        this.context = context;
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .displayer(new FadeInBitmapDisplayer(100)) // 展现方式：渐现
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
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
            holer.text = (TextView) convertView.findViewById(R.id.comment_text);
            holer.head = (ImageView) convertView.findViewById(R.id.userhead);
            convertView.setTag(holer);
        } else {
            holer = (Holer) convertView.getTag();
        }

        Comment comment = comments.get(position);
        holer.name.setText(comment.user.screen_name);
        ImageLoader.getInstance().displayImage(comment.user.profile_image_url, holer.head, options);
        holer.time.setText(DateUtil.GmtToDatastring(comment.created_at).substring(5, 16));
        holer.text.setText(StringUtil.ToDBC(comment.text));

        return convertView;
    }

    class Holer {
        TextView name, time, count, text;
        ImageView head;
    }

}
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
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.sina.weibo.sdk.openapi.models.User;

import java.util.List;

/**
 * Created by laizuhong on 2015/10/30.
 */
public class FriendAdapter extends BaseAdapter {

    Context context;
    List<User> users;
    DisplayImageOptions options;

    public FriendAdapter(Context context, List<User> users) {
        this.context = context;
        this.users = users;
        options = new DisplayImageOptions.Builder()
//                .showImageOnLoading(R.drawable.logo)
//                .showImageForEmptyUri(R.drawable.logo)
//                .showImageOnFail(R.drawable.logo)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .displayer(new FadeInBitmapDisplayer(100)) // 展现方式：渐现
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int position) {
        return users.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.item_friends, null);
        ImageView head = (ImageView) convertView.findViewById(R.id.userhead);
        TextView name = (TextView) convertView.findViewById(R.id.username);
        TextView desc = (TextView) convertView.findViewById(R.id.desc);

        User user = users.get(position);
        ImageLoader.getInstance().displayImage(user.profile_image_url, head, options);
        name.setText(user.screen_name);
        desc.setText(user.description);

        return convertView;
    }
}

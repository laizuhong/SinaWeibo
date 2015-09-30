package com.example.laizuhong.sinaweibo.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.laizuhong.sinaweibo.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.util.ArrayList;

/**
 * Created by laizuhong on 2015/9/30.
 */
public class MyGridviewAdapter extends BaseAdapter {


    ArrayList<String> pic;
    Context context;
    DisplayImageOptions options;

    public MyGridviewAdapter(ArrayList<String> pic, Context context) {
        this.pic = pic;
        this.context = context;
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
        if (pic.size() > 9) {
            return 9;
        }
        return pic.size();
    }

    @Override
    public Object getItem(int position) {
        return pic.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.item_gridview, null);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.image);
        com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(pic.get(position), imageView, options);
        return convertView;
    }

}

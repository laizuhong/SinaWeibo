package com.example.laizuhong.sinaweibo.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;

import com.bm.library.PhotoView;
import com.example.laizuhong.sinaweibo.R;
import com.example.laizuhong.sinaweibo.util.MyLog;
import com.example.laizuhong.sinaweibo.util.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.util.ArrayList;

/**
 * Created by laizuhong on 2015/9/30.
 */
public class MyGridviewAdapter extends BaseAdapter {

    ArrayList<String> pic;
    Context context;
    DisplayImageOptions options;
    GridView gridView;

    public MyGridviewAdapter(ArrayList<String> pic, Context context, GridView gridView) {
        this.pic = pic;
        this.context = context;
        this.gridView = gridView;
        options = new DisplayImageOptions.Builder()
//                .showImageOnLoading(R.drawable.logo)
//                .showImageForEmptyUri(R.drawable.logo)
//                .showImageOnFail(R.drawable.logo)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .displayer(new FadeInBitmapDisplayer(500)) // 展现方式：渐现
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
    public View getView(final int position, View convertView, ViewGroup parent) {


        String url = Utils.getUrl(pic.get(position));
        url = url.replace("thumbnail", "bmiddle");


        MyLog.e("getview url", url + "");
        convertView = LayoutInflater.from(context).inflate(R.layout.item_gridview, null);
        PhotoView p = (PhotoView) convertView.findViewById(R.id.image);
        if (p.getTag() == null || !p.getTag().equals(url)) {
            p.setTag(url);
            ImageLoader.getInstance().displayImage(url, p, options);
            p.disenable();
        }
        return convertView;
    }

}

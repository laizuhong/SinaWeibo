package com.example.laizuhong.sinaweibo;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.laizuhong.sinaweibo.bean.Picture;
import com.example.laizuhong.sinaweibo.bean.PictureFile;
import com.example.laizuhong.sinaweibo.util.DisplayUtil;
import com.example.laizuhong.sinaweibo.util.PictureUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.util.List;

/**
 * Created by laizuhong on 2015/9/18.
 */
public class PictureActivity extends AppCompatActivity implements View.OnClickListener {


    MyAdapter adapter;
    List<PictureFile> pictureFiles;
    List<Picture> pictures;
    GridView gridView;
    PopupWindow popupWindow;

    DisplayImageOptions options;
    ImageSize imageSize;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);
        init();
    }


    private void init() {
        pictureFiles = PictureUtil.newInstance(this).getFolders();
        pictures = pictureFiles.get(0).getSets();
        pictures.remove(0);
        imageSize = new ImageSize(DisplayUtil.dip2px(this, 50), DisplayUtil.dip2px(this, 50));
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.logo)
                .showImageForEmptyUri(R.drawable.logo)
                .showImageOnFail(R.drawable.logo)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .displayer(new FadeInBitmapDisplayer(100)) // 展现方式：渐现
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        gridView = (GridView) findViewById(R.id.picture);

        adapter = new MyAdapter();
        gridView.setAdapter(adapter);
        // gridView.setOnScrollListener(this);
        findViewById(R.id.back).setOnClickListener(this);
        findViewById(R.id.tv_title).setOnClickListener(this);
        initPopupWindow();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.tv_title:
                if (popupWindow.isShowing()) {
                    popupWindow.dismiss();
                } else {
                    popupWindow.showAsDropDown(v);
                }
                break;
        }
    }

    private void initPopupWindow() {

        // 一个自定义的布局，作为显示的内容
        View contentView = LayoutInflater.from(this).inflate(
                R.layout.menu_listview, null);
        // 设置按钮的点击事件

        ListView listView = (ListView) contentView.findViewById(R.id.listview);
        listView.setAdapter(new PopAdapter());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                popupWindow.dismiss();
                pictures = pictureFiles.get(position).getSets();
                adapter.notifyDataSetChanged();
                gridView.setSelection(0);
            }
        });
        View background = contentView.findViewById(R.id.background);
        background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        popupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);

        popupWindow.setTouchable(true);

        popupWindow.setTouchInterceptor(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                Log.i("mengdd", "onTouch : ");

                return false;
                // 这里如果返回true的话，touch事件将被拦截
                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
            }
        });

        popupWindow.setOutsideTouchable(true);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pictureFiles.clear();
        pictureFiles = null;
        pictures.clear();
        pictures = null;
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return pictures.size();
        }

        @Override
        public Object getItem(int position) {
            return pictures.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHoler holer;
            if (convertView == null) {
                holer = new ViewHoler();
                convertView = LayoutInflater.from(PictureActivity.this).inflate(R.layout.item_pic_gridview, null);
                holer.imageView = (ImageView) convertView.findViewById(R.id.imageview);
                convertView.setTag(holer);
            } else {
                holer = (ViewHoler) convertView.getTag();
                holer.imageView.setImageBitmap(null);
            }
            String path = pictures.get(position).getPath();
            if (path == null) {
                return convertView;
            }
            Log.e("path", path + "   123");

            ImageLoader.getInstance().displayImage("file://" + path, holer.imageView, options);
            return convertView;
        }
    }

    class ViewHoler {
        ImageView imageView;
    }

    class PopAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return pictureFiles.size();
        }

        @Override
        public Object getItem(int position) {
            return pictureFiles.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(PictureActivity.this).inflate(R.layout.item_menu_listview, null);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.image);
            TextView name = (TextView) convertView.findViewById(R.id.filename);
            TextView count = (TextView) convertView.findViewById(R.id.filecount);


            String path = pictureFiles.get(position).getThumbnail();
            Log.e("thumbnai", path);


            ImageLoader.getInstance().displayImage("file://" + path, imageView, options);
            name.setText(pictureFiles.get(position).getFolderName());
            count.setText("共" + pictureFiles.get(position).getCount() + "张");

            return convertView;
        }
    }


}

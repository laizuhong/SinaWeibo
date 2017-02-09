package com.example.laizuhong.sinaweibo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.laizuhong.sinaweibo.bean.Picture;
import com.example.laizuhong.sinaweibo.util.DisplayUtil;
import com.example.laizuhong.sinaweibo.util.PictureUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by laizuhong on 2015/9/18.
 */
public class PictureActivity extends AppCompatActivity implements View.OnClickListener {

    int tab = 0;
    MyAdapter adapter;
    //List<PictureFile> PictureUtil.pictureFiles;
    List<Picture> pictures;
    GridView gridView;
    PopupWindow popupWindow;

    DisplayImageOptions options;
    ImageSize imageSize;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.actionbar_back);
        actionBar.setTitle("图片");
        pictures = (List<Picture>) getIntent().getSerializableExtra("picture");
        if (pictures == null) {
            pictures = new ArrayList<>();
        } else {
            for (int i = 0; i < pictures.size(); i++) {
                Log.e("asdf", pictures.get(i).toString());
            }
        }
        init();
    }


    private void init() {
        PictureUtil.newInstance(this);


        imageSize = new ImageSize(DisplayUtil.dip2px(this, 50), DisplayUtil.dip2px(this, 50));
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .displayer(new FadeInBitmapDisplayer(100)) // 展现方式：渐现
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        gridView = (GridView) findViewById(R.id.picture);

        adapter = new MyAdapter();
        gridView.setAdapter(adapter);
        findViewById(R.id.chose).setOnClickListener(this);
        initPopupWindow();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chose_picture, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.ok) {
            Intent intent = new Intent();
            intent.putExtra("picture", (Serializable) pictures);
            setResult(RESULT_OK, intent);
            finish();
        } else if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.chose:
                if (popupWindow.isShowing()) {
                    popupWindow.dismiss();
                } else {
                    int[] location = new int[2];
                    v.getLocationOnScreen(location);

                    popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, location[0], location[1] - popupWindow.getHeight());

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
                tab = position;
                popupWindow.dismiss();
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
        int width = getWindowManager().getDefaultDisplay().getWidth();
        popupWindow = new PopupWindow(contentView, width, 800, true);
        popupWindow.setAnimationStyle(R.style.mypopwindow_anim_style);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
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


    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return PictureUtil.pictureFiles.get(tab).getSets().size();
        }

        @Override
        public Object getItem(int position) {
            return PictureUtil.pictureFiles.get(tab).getSets().get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHoler holer;
            if (convertView == null) {
                holer = new ViewHoler();
                convertView = LayoutInflater.from(PictureActivity.this).inflate(R.layout.item_pic_gridview, null);
                holer.imageView = (ImageView) convertView.findViewById(R.id.imageview);
                holer.check_layout = (LinearLayout) convertView.findViewById(R.id.check_layout);
                holer.box = (ImageView) convertView.findViewById(R.id.checkbox);
                convertView.setTag(holer);
            } else {
                holer = (ViewHoler) convertView.getTag();
                holer.imageView.setImageBitmap(null);
            }
            final Picture picture = PictureUtil.pictureFiles.get(tab).getSets().get(position);
            String path = picture.getPath();
            if (path == null) {
                return convertView;
            }
            Log.e("path", picture.toString() + "   123");

            ImageLoader.getInstance().displayImage("file://" + path, holer.imageView, options);
            if (picture.isChecked()) {
                holer.box.setBackgroundResource(R.drawable.compose_guide_check_box_right);
            } else {
                holer.box.setBackgroundResource(R.drawable.compose_guide_check_box_default);
            }

            holer.check_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (picture.isChecked()) {
                        Log.e("box取消", picture.isChecked() + "!!!");
                        holer.box.setBackgroundResource(R.drawable.compose_guide_check_box_default);
                        PictureUtil.pictureFiles.get(tab).getSets().get(position).isChecked = false;
                        for (int i = 0; i < pictures.size(); i++) {
                            if (pictures.get(i).getId() == picture.getId()) {
                                pictures.remove(picture);
                                break;
                            }
                        }
                    } else {
                        if (pictures.size() > 8) {
                            Toast.makeText(PictureActivity.this, "最多可选9张图", Toast.LENGTH_LONG).show();
                            return;
                        }
                        Log.e("box选中", picture.isChecked() + "!!!");
                        holer.box.setBackgroundResource(R.drawable.compose_guide_check_box_right);
                        PictureUtil.pictureFiles.get(tab).getSets().get(position).isChecked = true;
                        pictures.add(picture);
                    }
                    Log.e("map.size", pictures.size() + "   !!!");
                }
            });

            return convertView;
        }
    }

    class ViewHoler {
        ImageView imageView;
        LinearLayout check_layout;
        ImageView box;
    }

    class PopAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return PictureUtil.pictureFiles.size();
        }

        @Override
        public Object getItem(int position) {
            return PictureUtil.pictureFiles.get(position);
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


            String path = PictureUtil.pictureFiles.get(position).getThumbnail();
            Log.e("thumbnai", path);


            ImageLoader.getInstance().displayImage("file://" + path, imageView, options);
            name.setText(PictureUtil.pictureFiles.get(position).getFolderName());
            count.setText("共" + PictureUtil.pictureFiles.get(position).getCount() + "张");

            return convertView;
        }
    }


}

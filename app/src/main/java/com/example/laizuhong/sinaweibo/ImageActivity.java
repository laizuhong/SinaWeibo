package com.example.laizuhong.sinaweibo;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bm.library.PhotoView;
import com.example.laizuhong.sinaweibo.util.ProgressBarCircularIndetermininate;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;

import java.util.List;

/**
 * Created by laizuhong on 2015/11/9.
 */
public class ImageActivity extends AppCompatActivity {

    ViewPager viewPager;
    List<String> imgsId;
    int position;
    TextView number, totel;
    ProgressBarCircularIndetermininate pro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        imgsId = getIntent().getStringArrayListExtra("image");
        position = getIntent().getIntExtra("positon", 0);
        viewPager = (ViewPager) findViewById(R.id.pager);
        number = (TextView) findViewById(R.id.number);
        totel = (TextView) findViewById(R.id.totle);
        pro = (ProgressBarCircularIndetermininate) findViewById(R.id.pro);
        totel.setText("/" + imgsId.size());
        viewPager.setPageMargin((int) (getResources().getDisplayMetrics().density * 15));
        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return imgsId.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {

                PhotoView view = new PhotoView(ImageActivity.this);
                view.enable();
//                view.setImageResource(imgsId.get(position));
                String url = imgsId.get(position).replace("thumbnail", "large");
                view.setTag(url);
//                ImageLoader.getInstance().displayImage(imgsId.get(position), view, MyApp.options);
                ImageLoader.getInstance().displayImage(url, view, MyApp.options, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String s, View view) {
                        pro.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onLoadingFailed(String s, View view, FailReason failReason) {

                    }


                    @Override
                    public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                        pro.setVisibility(View.GONE);
                        PhotoView photoView = (PhotoView) view;
//                        ViewPager.LayoutParams params = (ViewPager.LayoutParams) photoView.getLayoutParams();
//                        if (bitmap.getHeight() > bitmap.getWidth()) {
//                            params.height = ViewPager.LayoutParams.MATCH_PARENT;
//                        } else {
//                            params.width = ViewPager.LayoutParams.MATCH_PARENT;
//                        }
//                        photoView.setLayoutParams(params);
                        photoView.setImageBitmap(bitmap);
                    }

                    @Override
                    public void onLoadingCancelled(String s, View view) {

                    }
                }, new ImageLoadingProgressListener() {
                    @Override
                    public void onProgressUpdate(String s, View view, int i, int i1) {

                    }
                });
                container.addView(view);
                return view;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }
        });

        viewPager.setCurrentItem(position);
        number.setText(position + 1 + "");
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                number.setText(position + 1 + "");
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


}

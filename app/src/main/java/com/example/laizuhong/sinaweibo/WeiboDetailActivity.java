package com.example.laizuhong.sinaweibo;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.laizuhong.sinaweibo.adapter.MyGridviewAdapter;
import com.example.laizuhong.sinaweibo.fragment.CommentFragment;
import com.example.laizuhong.sinaweibo.util.CatnutUtils;
import com.example.laizuhong.sinaweibo.util.DateUtil;
import com.example.laizuhong.sinaweibo.util.DisplayUtil;
import com.example.laizuhong.sinaweibo.util.MyGridView;
import com.example.laizuhong.sinaweibo.util.MyLog;
import com.example.laizuhong.sinaweibo.util.MyScrollView;
import com.example.laizuhong.sinaweibo.util.StringUtil;
import com.example.laizuhong.sinaweibo.util.TweetImageSpan;
import com.example.laizuhong.sinaweibo.util.TweetTextView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.sina.weibo.sdk.openapi.models.Status;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;

/**
 * Created by laizuhong on 2015/9/29.
 */
public class WeiboDetailActivity extends BaseActivity implements View.OnClickListener {

    Status status;
    TweetTextView text, frome_text;
    TextView name, time, frome, share_count, comment_count, like_count;
    ImageView head;
    LinearLayout share, commit, like, frome_status_layout;
    MyGridView gridView, frome_grid;
    DisplayImageOptions options;
    MyGridviewAdapter adapter;
    CommentFragment commentFragment;
    Fragment repostFragment;
    TweetImageSpan tweetImageSpan;
    PtrFrameLayout ptrFrameLayout;
    MyScrollView scrollView;
    FrameLayout frameLayout;
    int stop;


    private String weibo_id;

    public String getWeibo_id() {
        return weibo_id;
    }


    public ScrollView getScrollView() {
        return scrollView;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weibo_detail);
        status = (Status) getIntent().getSerializableExtra("weibo");
        init();
    }


    private void init() {
        getSupportActionBar().setTitle("正文");
        weibo_id = status.id;
//        findViewById(R.id.back).setOnClickListener(this);
        name = (TextView) findViewById(R.id.username);
        name.setOnClickListener(this);
        time = (TextView) findViewById(R.id.time);
        frome = (TextView) findViewById(R.id.frome);
        text = (TweetTextView) findViewById(R.id.text);
        share_count = (TextView) findViewById(R.id.share_count);
        comment_count = (TextView) findViewById(R.id.comment_count);
        like_count = (TextView) findViewById(R.id.like_count);
        head = (ImageView) findViewById(R.id.userhead);
        head.setOnClickListener(this);
        gridView = (MyGridView) findViewById(R.id.mygridview);
        share = (LinearLayout) findViewById(R.id.retweet);
        share.setOnClickListener(this);
        commit = (LinearLayout) findViewById(R.id.comment);
        commit.setOnClickListener(this);
        like = (LinearLayout) findViewById(R.id.like);
        like.setOnClickListener(this);

        tweetImageSpan = new TweetImageSpan(this);

        frome_text = (TweetTextView) findViewById(R.id.frome_text);
        frome_status_layout = (LinearLayout) findViewById(R.id.frome_status);
        frome_grid = (MyGridView) findViewById(R.id.frome_grid);

        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .displayer(new FadeInBitmapDisplayer(100)) // 展现方式：渐现
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        ptrFrameLayout = (PtrFrameLayout) findViewById(R.id.store_house_ptr_frame);
        // header
        final MaterialHeader header = new MaterialHeader(this);
        int[] colors = getResources().getIntArray(R.array.google_colors);
        header.setColorSchemeColors(colors);
        header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
        header.setPadding(0, 0, 0, DisplayUtil.dip2px(this, 10));
        header.setPtrFrameLayout(ptrFrameLayout);

        ptrFrameLayout.setResistance(1.7f);
        ptrFrameLayout.setRatioOfHeaderHeightToRefresh(1.2f);
        ptrFrameLayout.setDurationToClose(200);

        ptrFrameLayout.setLoadingMinTime(1000);
        ptrFrameLayout.setDurationToCloseHeader(500);
        ptrFrameLayout.setHeaderView(header);
        ptrFrameLayout.addPtrUIHandler(header);
        ptrFrameLayout.setPtrHandler(new PtrHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                frame.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        ptrFrameLayout.refreshComplete();
                    }
                }, 0);
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }
        });


        scrollView = (MyScrollView) findViewById(R.id.observable);
//        scrollView.smoothScrollBy(0, 0);
//        scrollView.setOnScrollBottomListener(new MyScrollView.OnScrollBottomListener() {
//            @Override
//            public void scrollBottom() {
//                MyLog.e("滚动到底部");
//                scrollView.requestDisallowInterceptTouchEvent(false);
//            }
//        });


        frameLayout = (FrameLayout) findViewById(R.id.layout);

        initDate();
    }


    private void initDate() {
        name.setText(status.user.screen_name);
        time.setText(DateUtil.GmtToDatastring(status.created_at).substring(5, 16));
        frome.setText(Html.fromHtml(status.source).toString());
        text.setText(status.text);
        CatnutUtils.vividTweet(text, tweetImageSpan);
        StringUtil.setTextview(text, this);
        share_count.setText("转发" + status.reposts_count);
        comment_count.setText("评论" + status.comments_count);
        like_count.setText("赞" + status.attitudes_count);
        ImageLoader.getInstance().displayImage(status.user.profile_image_url, head, options);
        if (status.pic_urls != null) {
            gridView.setVisibility(View.VISIBLE);
            adapter = new MyGridviewAdapter(status.pic_urls, this);
            gridView.setAdapter(adapter);
        } else {
            gridView.setVisibility(View.GONE);
        }

        if (status.retweeted_status == null) {
            frome_status_layout.setVisibility(View.GONE);
        } else {
            frome_status_layout.setVisibility(View.VISIBLE);
            frome_text.setText(status.retweeted_status.text);
            CatnutUtils.vividTweet(frome_text, tweetImageSpan);
            StringUtil.setTextview(frome_text, this);
            if (status.retweeted_status.pic_urls == null) {
                frome_grid.setVisibility(View.GONE);
            } else {
                frome_grid.setVisibility(View.VISIBLE);
                adapter = new MyGridviewAdapter(status.retweeted_status.pic_urls, this);
                frome_grid.setAdapter(adapter);
            }
        }


        stop = DisplayUtil.dip2px(this, 40);


        final RelativeLayout center = (RelativeLayout) findViewById(R.id.center);
        ViewTreeObserver vto = center.getViewTreeObserver();

        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            boolean hasMeasured = false;

            public boolean onPreDraw() {
                if (hasMeasured == false) {

                    int height = center.getMeasuredHeight();
                    //获取到宽度和高度后，可用于计算
                    Log.e("onPreDraw", height + "");

                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) frameLayout.getLayoutParams();
                    MyLog.e(height + "   " + stop);
                    params.height = height - stop;
                    params.width = LinearLayout.LayoutParams.MATCH_PARENT;
                    frameLayout.setLayoutParams(params);
                    hasMeasured = true;

                }
                return true;
            }
        });


//        vto=topview.getViewTreeObserver();
//        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
//            boolean hasMeasured=false;
//            @Override
//            public boolean onPreDraw() {
//                if (!hasMeasured){
//                    top=topview.getMeasuredHeight();
//                    hasMeasured=true;
//                }
//                return true;
//            }
//        });

//        vto=stopView.getViewTreeObserver();
//        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
//            boolean hasMeasured=false;
//            @Override
//            public boolean onPreDraw() {
//                if (!hasMeasured){
//                    stop=stopView.getMeasuredHeight();
//                    hasMeasured=true;
//                }
//                return true;
//            }
//        });


//        scrollView.setOnScrollListener(new MyScrollView.OnScrollListener() {
//            @Override
//            public void onScroll(int scrollY) {
//                Log.e("onScroll", scrollY + "     " + top + "   " + stop+"   "+stopView.getTop());
//                if (scrollY> top) {
//                    topstopview.setVisibility(View.VISIBLE);
//                }
//                if (scrollY < top) {
//                    topstopview.setVisibility(View.GONE);
//                }
//            }
//        });
        switchFragment(1);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
        }
    }

    public void switchFragment(int tab) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        switch (tab) {
            case 1:
                if (commentFragment == null) {
                    commentFragment = new CommentFragment();
                    ft.add(R.id.layout, commentFragment);
                } else {
                    ft.show(commentFragment);
                }
                break;
            default:
                break;
        }
        ft.commit();
    }


}

package com.example.laizuhong.sinaweibo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
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
import com.example.laizuhong.sinaweibo.fragment.LikeFragment;
import com.example.laizuhong.sinaweibo.fragment.RespotFragment;
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

    public static boolean butom = false;
    Status status;
    TweetTextView text, frome_text;
    TextView name, time, frome, share_count, comment_count, like_count;
    ImageView head;
    LinearLayout share, commit, like, frome_status_layout;
    MyGridView gridView, frome_grid;
    DisplayImageOptions options;
    MyGridviewAdapter adapter;
    CommentFragment commentFragment;
    RespotFragment repostFragment;
    LikeFragment likeFragment;
    TweetImageSpan tweetImageSpan;
    PtrFrameLayout ptrFrameLayout;
    MyScrollView scrollView;
    FrameLayout frameLayout1, frameLayout2, frameLayout3;
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
        scrollView.smoothScrollBy(0, 0);
        scrollView.setOnScrollBottomListener(new MyScrollView.OnScrollBottomListener() {
            @Override
            public void scrollBottom(boolean isBotton) {
                //scrollView.requestDisallowInterceptTouchEvent(false);
                if (isBotton) {
                    MyLog.e("滚动到底部");
                    butom = true;
                } else {
                    butom = false;
                }
            }
        });


        frameLayout1 = (FrameLayout) findViewById(R.id.layout1);
        frameLayout2 = (FrameLayout) findViewById(R.id.layout2);
        frameLayout3 = (FrameLayout) findViewById(R.id.layout3);

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
        share_count.setOnClickListener(this);
        comment_count.setText("评论" + status.comments_count);
        comment_count.setOnClickListener(this);
        like_count.setText("赞" + status.attitudes_count);
        like_count.setOnClickListener(this);
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

                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) frameLayout1.getLayoutParams();
                    MyLog.e(height + "   " + stop);
                    params.height = height - stop;
                    params.width = LinearLayout.LayoutParams.MATCH_PARENT;
                    frameLayout1.setLayoutParams(params);
                    frameLayout2.setLayoutParams(params);
                    frameLayout3.setLayoutParams(params);
                    hasMeasured = true;

                }
                return true;
            }
        });

        switchFragment(2);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.share_count:
                switchFragment(1);
                break;
            case R.id.comment_count:
                switchFragment(2);
                break;
            case R.id.like_count:
                switchFragment(3);
                break;
            case R.id.retweet:
                Intent intent = new Intent(WeiboDetailActivity.this, SendWeiboActivity.class);
                intent.putExtra("weibo", status);
                startActivity(intent);
                break;
            case R.id.comment:
                Intent comment = new Intent(WeiboDetailActivity.this, SendWeiboActivity.class);
                comment.putExtra("weibo", status);
                comment.putExtra("comment", true);
                startActivity(comment);
                break;
            case R.id.like:

                break;
        }
    }

    public void switchFragment(int tab) {

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        clearFouce();
        //hideFragments(ft);
        switch (tab) {
            case 1:
                share_count.setTextColor(getResources().getColor(R.color.black));
                if (repostFragment == null) {
                    repostFragment = new RespotFragment();
                    ft.replace(R.id.layout1, repostFragment);
                }
                frameLayout1.setVisibility(View.VISIBLE);
                break;
            case 2:
                comment_count.setTextColor(getResources().getColor(R.color.black));
                if (commentFragment == null) {
                    commentFragment = new CommentFragment();
                    ft.replace(R.id.layout2, commentFragment);
                }
                frameLayout2.setVisibility(View.VISIBLE);
                break;
            case 3:
                like_count.setTextColor(getResources().getColor(R.color.black));
                if (likeFragment == null) {
                    likeFragment = new LikeFragment();
                    ft.replace(R.id.layout3, likeFragment);
                }
                frameLayout3.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
        ft.commit();
    }


    /**
     * 将所有的Fragment都置为隐藏状态。
     *
     * @param transaction 用于对Fragment执行操作的事务
     */
    private void hideFragments(FragmentTransaction transaction) {
        if (repostFragment != null) {
            transaction.hide(repostFragment);
        }
        if (commentFragment != null) {
            transaction.hide(commentFragment);
        }
        if (likeFragment != null) {
            transaction.hide(likeFragment);
        }

    }


    private void clearFouce() {
        frameLayout1.setVisibility(View.GONE);
        frameLayout2.setVisibility(View.GONE);
        frameLayout3.setVisibility(View.GONE);
        share_count.setTextColor(getResources().getColor(R.color.tab_back));
        comment_count.setTextColor(getResources().getColor(R.color.tab_back));
        like_count.setTextColor(getResources().getColor(R.color.tab_back));
    }
}

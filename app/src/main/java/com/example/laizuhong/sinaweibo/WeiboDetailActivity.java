package com.example.laizuhong.sinaweibo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.laizuhong.sinaweibo.adapter.CommentAdapter;
import com.example.laizuhong.sinaweibo.adapter.MyGridviewAdapter;
import com.example.laizuhong.sinaweibo.config.AccessTokenKeeper;
import com.example.laizuhong.sinaweibo.util.CatnutUtils;
import com.example.laizuhong.sinaweibo.util.DateUtil;
import com.example.laizuhong.sinaweibo.util.DisplayUtil;
import com.example.laizuhong.sinaweibo.util.MyGridView;
import com.example.laizuhong.sinaweibo.util.MyLog;
import com.example.laizuhong.sinaweibo.util.StringUtil;
import com.example.laizuhong.sinaweibo.util.TweetImageSpan;
import com.example.laizuhong.sinaweibo.util.TweetTextView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.CommentsAPI;
import com.sina.weibo.sdk.openapi.models.Comment;
import com.sina.weibo.sdk.openapi.models.CommentList;
import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;

/**
 * Created by laizuhong on 2015/9/29.
 */
public class WeiboDetailActivity extends BaseActivity implements View.OnClickListener, AbsListView.OnScrollListener {

    Status status;
    TweetTextView text, frome_text;
    TextView name, time, frome;
    ImageView head;
    LinearLayout share, commit, like, frome_status_layout;
    MyGridView gridView, frome_grid;
    DisplayImageOptions options;
    MyGridviewAdapter adapter;
    TweetImageSpan tweetImageSpan;
    PtrFrameLayout ptrFrameLayout;
    FrameLayout frameLayout;
    CommentsAPI commentsAPI;
    Oauth2AccessToken oauth2AccessToken;
    View headview;
    ListView listView;
    int page = 1;
    CommentAdapter commentAdapter;
    List<Comment> commentlist;
    boolean fresh = false;
    private long weibo_id;
    private View mProgressBar;
    private TextView mHintView;
    private View footview, rootView;

    /**
     * 微博 OpenAPI 回调接口。
     */
    private RequestListener mListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            MyLog.e("onComplete", response);
            if (!TextUtils.isEmpty(response)) {
                CommentList comments = CommentList.parse(response);
                if (comments != null && comments.total_number > 0 && comments.commentList != null && comments.commentList.size() > 0) {

                    commentlist.addAll(comments.commentList);
                    commentAdapter.notifyDataSetChanged();
                    fresh = false;
                    setState(0);
                } else if (comments != null || comments.total_number == 0 || comments.commentList == null || comments.commentList.size() == 0) {
                    setState(0);
                }
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            LogUtil.e("onWeiboException", e.getMessage());
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weibo_detail);
        status = (Status) getIntent().getSerializableExtra("weibo");
        init();
    }


    private void init() {
        getSupportActionBar().setTitle("正文");
        weibo_id = Long.valueOf(status.id);

        headview = LayoutInflater.from(this).inflate(R.layout.weibo_detail, null);
        name = (TextView) headview.findViewById(R.id.username);
        name.setOnClickListener(this);
        time = (TextView) headview.findViewById(R.id.time);
        frome = (TextView) headview.findViewById(R.id.frome);
        text = (TweetTextView) headview.findViewById(R.id.text);
        head = (ImageView) headview.findViewById(R.id.userhead);
        head.setOnClickListener(this);
        gridView = (MyGridView) headview.findViewById(R.id.mygridview);
        share = (LinearLayout) findViewById(R.id.retweet);
        share.setOnClickListener(this);
        commit = (LinearLayout) findViewById(R.id.comment);
        commit.setOnClickListener(this);
        like = (LinearLayout) findViewById(R.id.like);
        like.setOnClickListener(this);

        tweetImageSpan = new TweetImageSpan(this);

        frome_text = (TweetTextView) headview.findViewById(R.id.frome_text);
        frome_status_layout = (LinearLayout) headview.findViewById(R.id.frome_status);
        frome_grid = (MyGridView) headview.findViewById(R.id.frome_grid);

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


        listView = (ListView) findViewById(R.id.layout);


        footview = LayoutInflater.from(this)
                .inflate(R.layout.xlistview_footer, null);
        mProgressBar = footview.findViewById(R.id.xlistview_footer_progressbar);
        mHintView = (TextView) footview
                .findViewById(R.id.xlistview_footer_hint_textview);

        commentlist = new ArrayList<>();
        commentAdapter = new CommentAdapter(commentlist, this);
        listView.addFooterView(footview);
        listView.addHeaderView(headview);
        listView.setAdapter(commentAdapter);
        listView.setOnScrollListener(this);



        initDate();
        // 获取当前已保存过的 Token
        oauth2AccessToken = AccessTokenKeeper.readAccessToken(this);
        // 获取微博评论信息接口
        commentsAPI = new CommentsAPI(this, Constants.APP_KEY, oauth2AccessToken);

        commentsAPI.show(weibo_id, 0, 0, 10, page, CommentsAPI.AUTHOR_FILTER_ALL, mListener);
    }


    private void initDate() {
        name.setText(status.user.screen_name);
        time.setText(DateUtil.GmtToDatastring(status.created_at).substring(5, 16));
        frome.setText(Html.fromHtml(status.source).toString());
        text.setText(status.text);
        CatnutUtils.vividTweet(text, tweetImageSpan);
        StringUtil.setTextview(text, this);
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


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

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


    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        MyLog.e("comment  onScroll", "first=" + firstVisibleItem + "    visible=" + visibleItemCount + "     total=" + totalItemCount);
        if (totalItemCount - firstVisibleItem == visibleItemCount && fresh == false) {
            footview.setVisibility(View.VISIBLE);
            fresh = true;
            setState(1);
            page++;
            commentsAPI.show(weibo_id, 0, 0, 10, page, CommentsAPI.AUTHOR_FILTER_ALL, mListener);
        }
    }

    public void request(boolean b) {
        listView.requestDisallowInterceptTouchEvent(b);
    }


    private void setState(int state) {
        mProgressBar.setVisibility(View.GONE);

        if (state == 0) {
            mHintView.setText("加载更多");
        } else if (state == 1) {// 当加载的时候
            mProgressBar.setVisibility(View.VISIBLE);// 加载进度条显示
            mHintView.setText("正在加载");
        }
    }

}

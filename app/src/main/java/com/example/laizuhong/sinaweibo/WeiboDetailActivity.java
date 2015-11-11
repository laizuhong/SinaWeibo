package com.example.laizuhong.sinaweibo;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.laizuhong.sinaweibo.adapter.CommentAdapter;
import com.example.laizuhong.sinaweibo.adapter.MyGridviewAdapter;
import com.example.laizuhong.sinaweibo.config.AccessTokenKeeper;
import com.example.laizuhong.sinaweibo.util.DisplayUtil;
import com.example.laizuhong.sinaweibo.util.MyLog;
import com.example.laizuhong.sinaweibo.util.TweetImageSpan;
import com.example.laizuhong.sinaweibo.util.WeiboDetailHeader;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
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

import butterknife.Bind;
import butterknife.ButterKnife;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;

/**
 * Created by laizuhong on 2015/9/29.
 */
public class WeiboDetailActivity extends BaseActivity implements View.OnClickListener, AbsListView.OnScrollListener {

    Status status;
    DisplayImageOptions options;
    MyGridviewAdapter adapter;
    TweetImageSpan tweetImageSpan;
    CommentsAPI commentsAPI;
    Oauth2AccessToken oauth2AccessToken;
    int page = 0;
    CommentAdapter commentAdapter;
    List<Comment> commentlist;
    boolean fresh = false;
    WeiboDetailHeader detailHeader;
    @Bind(R.id.listview)
    ListView listView;
    @Bind(R.id.store_house_ptr_frame)
    PtrFrameLayout storeHousePtrFrame;
    @Bind(R.id.center)
    RelativeLayout center;
    @Bind(R.id.retweetcount)
    TextView retweetcount;
    @Bind(R.id.retweet)
    LinearLayout retweet;
    @Bind(R.id.commentcount)
    TextView commentcount;
    @Bind(R.id.comment)
    LinearLayout comment;
    @Bind(R.id.likeimage)
    ImageView likeimage;
    @Bind(R.id.likecount)
    TextView likecount;
    @Bind(R.id.like)
    LinearLayout like;
    @Bind(R.id.fun_layout)
    LinearLayout funLayout;
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
        ButterKnife.bind(this);
        status = (Status) getIntent().getSerializableExtra("weibo");
        MyLog.e(status.toString());
        init();
    }


    private void init() {
        getSupportActionBar().setTitle("正文");

        weibo_id = Long.valueOf(status.id);
        retweet.setOnClickListener(this);
        comment.setOnClickListener(this);
        like.setOnClickListener(this);

        tweetImageSpan = new TweetImageSpan(this);
        detailHeader = new WeiboDetailHeader(this);
        detailHeader.initDate(status, tweetImageSpan);


        // header
        final MaterialHeader header = new MaterialHeader(this);
        int[] colors = getResources().getIntArray(R.array.google_colors);
        header.setColorSchemeColors(colors);
        header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
        header.setPadding(0, 0, 0, DisplayUtil.dip2px(this, 10));
        header.setPtrFrameLayout(storeHousePtrFrame);

        storeHousePtrFrame.setResistance(1.7f);
        storeHousePtrFrame.setRatioOfHeaderHeightToRefresh(1.2f);
        storeHousePtrFrame.setDurationToClose(200);

        storeHousePtrFrame.setLoadingMinTime(1000);
        storeHousePtrFrame.setDurationToCloseHeader(500);
        storeHousePtrFrame.setHeaderView(header);
        storeHousePtrFrame.addPtrUIHandler(header);
        storeHousePtrFrame.setPtrHandler(new PtrHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                frame.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        storeHousePtrFrame.refreshComplete();
                    }
                }, 0);
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }
        });

        footview = LayoutInflater.from(this)
                .inflate(R.layout.xlistview_footer, null);
        mProgressBar = footview.findViewById(R.id.xlistview_footer_progressbar);
        mHintView = (TextView) footview
                .findViewById(R.id.xlistview_footer_hint_textview);

        commentlist = new ArrayList<>();
        commentAdapter = new CommentAdapter(commentlist, this);
        listView.addFooterView(footview);
        listView.addHeaderView(detailHeader, null, false);
        listView.setAdapter(commentAdapter);
        listView.setOnScrollListener(this);



        // 获取当前已保存过的 Token
        oauth2AccessToken = AccessTokenKeeper.readAccessToken(this);
        // 获取微博评论信息接口
        commentsAPI = new CommentsAPI(this, Constants.APP_KEY, oauth2AccessToken);

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
            case R.id.frome_status:
                Intent weibo = new Intent(WeiboDetailActivity.this, WeiboDetailActivity.class);
                weibo.putExtra("weibo", status.retweeted_status);
                startActivity(weibo);
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
            fresh = true;
            footview.setVisibility(View.VISIBLE);
            setState(1);
            page++;
            MyLog.e("onscroll 刷新了");
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

package com.example.laizuhong.sinaweibo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.laizuhong.sinaweibo.adapter.CommentAdapter;
import com.example.laizuhong.sinaweibo.adapter.MyGridviewAdapter;
import com.example.laizuhong.sinaweibo.config.AccessTokenKeeper;
import com.example.laizuhong.sinaweibo.util.CatnutUtils;
import com.example.laizuhong.sinaweibo.util.DateUtil;
import com.example.laizuhong.sinaweibo.util.MyGridView;
import com.example.laizuhong.sinaweibo.util.MyLog;
import com.example.laizuhong.sinaweibo.util.MyToast;
import com.example.laizuhong.sinaweibo.util.TweetImageSpan;
import com.example.laizuhong.sinaweibo.util.TweetTextView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.CommentsAPI;
import com.sina.weibo.sdk.openapi.legacy.FavoritesAPI;
import com.sina.weibo.sdk.openapi.models.Comment;
import com.sina.weibo.sdk.openapi.models.CommentList;
import com.sina.weibo.sdk.openapi.models.Status;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by laizuhong on 2015/9/29.
 */
public class WeiboDetailActivity extends AppCompatActivity implements View.OnClickListener, AbsListView.OnScrollListener {

    Status status;
//    DisplayImageOptions options;
    MyGridviewAdapter adapter;
    TweetImageSpan tweetImageSpan;
    CommentsAPI commentsAPI;
    FavoritesAPI favoritesAPI;
    Oauth2AccessToken oauth2AccessToken;
    int page = 0;
    CommentAdapter commentAdapter;
    List<Comment> commentlist;
    boolean fresh = false;
    @Bind(R.id.listview)
    ListView listView;
    @Bind(R.id.center)
    LinearLayout center;
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
    @Bind(R.id.tvlike)
    TextView tvlike;
    @Bind(R.id.like)
    LinearLayout like;
    @Bind(R.id.fun_layout)
    LinearLayout funLayout;
//    @Bind(R.id.head)
//    LinearLayout head;
    LinearLayout view;
    ImageView userhead;
    TextView username;
    TextView time;
    TextView frome;
    TweetTextView text;
    MyGridView mygridview;
    TweetTextView fromeText;
    MyGridView fromeGrid;
    TextView fromeShareCount;
    LinearLayout fromeShareLayout;
    TextView fromeCommentCount;
    LinearLayout fromeCommentLayout;
    CardView fromeStatus;
    TextView shareCount;
    TextView commentCount;
    LinearLayout topview;
    private long weibo_id;
    private View mProgressBar;
    private TextView mHintView;
    private View footview, rootView;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    /**
     * 微博 OpenAPI 回调接口。
     */
    private RequestListener mListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            MyLog.e("onComplete", response);
            if (response.startsWith("{\"statuses\"")) {
                MyToast.makeText("收藏成功");
            }
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
            MyLog.e("onWeiboException", e.getMessage());
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weibo_detail);
        ButterKnife.bind(this);
        toolbar.setTitle("正文");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        status = (Status) getIntent().getSerializableExtra("weibo");
        MyLog.e(status.toString());
        init();
    }


    private void init() {
        //getSupportActionBar().setTitle("正文");

        weibo_id = Long.valueOf(status.id);
        retweet.setOnClickListener(this);
        comment.setOnClickListener(this);
        like.setOnClickListener(this);

        tweetImageSpan = new TweetImageSpan(this);
//        detailHeader = new WeiboDetailHeader(this);
//        detailHeader.initDate(status, tweetImageSpan);
        initHeaderView();

        //head.addView(detailHeader);

        footview = LayoutInflater.from(this)
                .inflate(R.layout.xlistview_footer, null);
        mProgressBar = footview.findViewById(R.id.xlistview_footer_progressbar);
        mHintView = (TextView) footview
                .findViewById(R.id.xlistview_footer_hint_textview);

        commentlist = new ArrayList<>();
        commentAdapter = new CommentAdapter(commentlist, this);
        listView.addFooterView(footview);
        listView.addHeaderView(view);
        listView.setAdapter(commentAdapter);
        listView.setOnScrollListener(this);


        initDate();

        // 获取当前已保存过的 Token
        oauth2AccessToken = AccessTokenKeeper.readAccessToken(this);
        favoritesAPI = new FavoritesAPI(this, Constants.APP_KEY, oauth2AccessToken);
        // 获取微博评论信息接口
        commentsAPI = new CommentsAPI(this, Constants.APP_KEY, oauth2AccessToken);

    }


    private void initHeaderView() {
        view = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.weibo_detail, null);
        userhead = (ImageView) view.findViewById(R.id.userhead);
        username = (TextView) view.findViewById(R.id.username);
        time = (TextView) view.findViewById(R.id.time);
        frome = (TextView) view.findViewById(R.id.frome);
        text = (TweetTextView) view.findViewById(R.id.text);
        mygridview = (MyGridView) view.findViewById(R.id.mygridview);
        fromeText = (TweetTextView) view.findViewById(R.id.frome_text);
        fromeGrid = (MyGridView) view.findViewById(R.id.frome_grid);
        fromeShareCount = (TextView) view.findViewById(R.id.frome_share_count);
        fromeShareLayout = (LinearLayout) view.findViewById(R.id.frome_share_layout);
        fromeCommentCount = (TextView) view.findViewById(R.id.frome_comment_count);
        fromeCommentLayout = (LinearLayout) view.findViewById(R.id.frome_comment_layout);
        fromeStatus = (CardView) view.findViewById(R.id.frome_status);
        shareCount = (TextView) view.findViewById(R.id.share_count);
        commentCount = (TextView) view.findViewById(R.id.comment_count);
        topview = (LinearLayout) view.findViewById(R.id.topview);
    }


    public void initDate() {

        if (status.favorited) {
            tvlike.setText("已收藏");
            likeimage.setBackgroundResource(R.drawable.fav_en_dark);
        } else {
            tvlike.setText("收藏");
            likeimage.setBackgroundResource(R.drawable.fav_en_light);
        }
        like.setOnClickListener(this);

        username.setText(status.user.screen_name);
        time.setText(DateUtil.GmtToDatastring(status.created_at).substring(5, 16));
        frome.setText(Html.fromHtml(status.source).toString());
        text.setText(status.text);
        CatnutUtils.vividTweet(text, tweetImageSpan);
        //StringUtil.setTextview(text, this);
        ImageLoader.getInstance().displayImage(status.user.profile_image_url, userhead, MyApp.options);
        if (status.pic_urls != null) {
            mygridview.setVisibility(View.VISIBLE);
            adapter = new MyGridviewAdapter(status.pic_urls, this, mygridview);
            mygridview.setAdapter(adapter);
            mygridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(WeiboDetailActivity.this, ImageActivity.class);
                    intent.putStringArrayListExtra("image", status.pic_urls);
                    intent.putExtra("positon", position);
                    startActivity(intent);
                }
            });
        } else {
            mygridview.setVisibility(View.GONE);
        }
        shareCount.setText(status.reposts_count + " 转发");
        commentCount.setText(status.comments_count + " 评论");
        if (status.retweeted_status == null) {
            fromeStatus.setVisibility(View.GONE);
        } else {
            fromeStatus.setVisibility(View.VISIBLE);
            fromeText.setText(status.retweeted_status.text);
            CatnutUtils.vividTweet(fromeText, tweetImageSpan);
            //StringUtil.setTextview(frome_text, this);
            if (status.retweeted_status.pic_urls == null) {
                fromeGrid.setVisibility(View.GONE);
            } else {
                fromeGrid.setVisibility(View.VISIBLE);
                adapter = new MyGridviewAdapter(status.retweeted_status.pic_urls, this, fromeGrid);
                fromeGrid.setAdapter(adapter);
                fromeGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(WeiboDetailActivity.this, ImageActivity.class);
                        intent.putStringArrayListExtra("image", status.pic_urls);
                        intent.putExtra("positon", position);
                        startActivity(intent);
                    }
                });
            }
            if (status.retweeted_status.reposts_count == 0) {
                fromeShareLayout.setVisibility(View.GONE);
            } else {
                fromeShareLayout.setVisibility(View.VISIBLE);
                fromeShareCount.setText(status.retweeted_status.reposts_count + "");
            }
            if (status.retweeted_status.comments_count == 0) {
                fromeCommentLayout.setVisibility(View.GONE);
            } else {
                fromeCommentLayout.setVisibility(View.VISIBLE);
                fromeCommentCount.setText(status.retweeted_status.comments_count + "");
            }
            fromeShareLayout.setOnClickListener(this);
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
            case R.id.frome_status:
                Intent weibo = new Intent(WeiboDetailActivity.this, WeiboDetailActivity.class);
                weibo.putExtra("weibo", status.retweeted_status);
                startActivity(weibo);
                break;
            case R.id.like:
                long id = Long.valueOf(status.id);
                if (tvlike.getText().toString().equals("收藏")) {
                    MyLog.e("添加收藏" + id);
                    favoritesAPI.create(id, mListener);
                } else {
                    MyLog.e("取消收藏" + id);
                    favoritesAPI.destroy(id, mListener);
                }
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:// 点击返回图标事件
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

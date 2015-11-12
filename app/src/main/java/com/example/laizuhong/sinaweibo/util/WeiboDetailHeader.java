package com.example.laizuhong.sinaweibo.util;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.laizuhong.sinaweibo.ImageActivity;
import com.example.laizuhong.sinaweibo.MyApp;
import com.example.laizuhong.sinaweibo.R;
import com.example.laizuhong.sinaweibo.WeiboDetailActivity;
import com.example.laizuhong.sinaweibo.adapter.MyGridviewAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sina.weibo.sdk.openapi.models.Status;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by laizuhong on 2015/11/11.
 */
public class WeiboDetailHeader extends LinearLayout implements View.OnClickListener {

    Context context;
    Status status;
    MyGridviewAdapter adapter;

    @Bind(R.id.userhead)
    ImageView userhead;
    @Bind(R.id.username)
    TextView username;
    @Bind(R.id.time)
    TextView time;
    @Bind(R.id.frome)
    TextView frome;
    @Bind(R.id.text)
    TweetTextView text;
    @Bind(R.id.mygridview)
    MyGridView mygridview;
    @Bind(R.id.frome_text)
    TweetTextView fromeText;
    @Bind(R.id.frome_grid)
    MyGridView fromeGrid;
    @Bind(R.id.frome_share_count)
    TextView fromeShareCount;
    @Bind(R.id.frome_share_layout)
    LinearLayout fromeShareLayout;
    @Bind(R.id.frome_comment_count)
    TextView fromeCommentCount;
    @Bind(R.id.frome_comment_layout)
    LinearLayout fromeCommentLayout;
    @Bind(R.id.frome_status)
    CardView fromeStatus;
    @Bind(R.id.share_count)
    TextView shareCount;
    @Bind(R.id.comment_count)
    TextView commentCount;
    @Bind(R.id.topview)
    LinearLayout topview;


//    public WeiboDetailHeader(Context context) {
//        super(context);
//        this.context=context;
//        initView(context);
//    }


    public WeiboDetailHeader(Context context) {
        super(context);
        this.context = context;
        initView(context);
    }

    public WeiboDetailHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView(context);
    }

    public WeiboDetailHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView(context);
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    private void initView(Context context) {
        LinearLayout view = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.weibo_detail, null);
        ButterKnife.bind(this, view);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        addView(view, lp);
    }

    public void initDate(Status s, TweetImageSpan tweetImageSpan) {
        this.status = s;
        username.setText(status.user.screen_name);
        time.setText(DateUtil.GmtToDatastring(status.created_at).substring(5, 16));
        frome.setText(Html.fromHtml(status.source).toString());
        text.setText(status.text);
        CatnutUtils.vividTweet(text, tweetImageSpan);
        //StringUtil.setTextview(text, this);
        ImageLoader.getInstance().displayImage(status.user.profile_image_url, userhead, MyApp.options);
        if (status.pic_urls != null) {
            mygridview.setVisibility(View.VISIBLE);
            adapter = new MyGridviewAdapter(status.pic_urls, context, mygridview);
            mygridview.setAdapter(adapter);
            mygridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(context, ImageActivity.class);
                    intent.putStringArrayListExtra("image", status.pic_urls);
                    intent.putExtra("positon", position);
                    context.startActivity(intent);
                }
            });
        } else {
            mygridview.setVisibility(View.GONE);
        }
        shareCount.setText(status.reposts_count + " 转发");
        commentCount.setText(status.comments_count + " 评论");
        if (status.retweeted_status == null) {
            fromeShareLayout.setVisibility(View.GONE);
        } else {
            fromeShareLayout.setVisibility(View.VISIBLE);
            fromeText.setText(status.retweeted_status.text);
            CatnutUtils.vividTweet(fromeText, tweetImageSpan);
            //StringUtil.setTextview(frome_text, this);
            if (status.retweeted_status.pic_urls == null) {
                fromeGrid.setVisibility(View.GONE);
            } else {
                fromeGrid.setVisibility(View.VISIBLE);
                adapter = new MyGridviewAdapter(status.retweeted_status.pic_urls, context, fromeGrid);
                fromeGrid.setAdapter(adapter);
                fromeGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(context, ImageActivity.class);
                        intent.putStringArrayListExtra("image", status.pic_urls);
                        intent.putExtra("positon", position);
                        context.startActivity(intent);
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
        if (v.getId() == R.id.frome_share_layout) {
            Intent weibo = new Intent(context, WeiboDetailActivity.class);
            weibo.putExtra("weibo", status.retweeted_status);
            context.startActivity(weibo);
        }
    }
}

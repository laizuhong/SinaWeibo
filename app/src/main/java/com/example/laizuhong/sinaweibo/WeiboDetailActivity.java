package com.example.laizuhong.sinaweibo;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.laizuhong.sinaweibo.adapter.MyGridviewAdapter;
import com.example.laizuhong.sinaweibo.fragment.CommentFragment;
import com.example.laizuhong.sinaweibo.util.DateUtil;
import com.example.laizuhong.sinaweibo.util.MyGridView;
import com.example.laizuhong.sinaweibo.util.StringUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.sina.weibo.sdk.openapi.models.Status;

/**
 * Created by laizuhong on 2015/9/29.
 */
public class WeiboDetailActivity extends AppCompatActivity implements View.OnClickListener {

    Status status;
    TextView name, time, frome, text, share_count, comment_count, like_count, frome_text;
    ImageView head, more;
    LinearLayout share, commit, like, frome_status_layout;
    MyGridView gridView, frome_grid;
    DisplayImageOptions options;
    MyGridviewAdapter adapter;
    Fragment commentFragment;
    Fragment repostFragment;
    private String weibo_id;

    public String getWeibo_id() {
        return weibo_id;
    }

    public void setWeibo_id(String weibo_id) {
        this.weibo_id = weibo_id;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weibo_detail);
        status = (Status) getIntent().getSerializableExtra("weibo");
        init();
    }


    private void init() {
        weibo_id = status.id;
        findViewById(R.id.back).setOnClickListener(this);
        name = (TextView) findViewById(R.id.username);
        name.setOnClickListener(this);
        time = (TextView) findViewById(R.id.time);
        frome = (TextView) findViewById(R.id.frome);
        text = (TextView) findViewById(R.id.text);
        share_count = (TextView) findViewById(R.id.share_count);
        comment_count = (TextView) findViewById(R.id.comment_count);
        like_count = (TextView) findViewById(R.id.like_count);
        head = (ImageView) findViewById(R.id.userhead);
        head.setOnClickListener(this);
        more = (ImageView) findViewById(R.id.more);
        more.setOnClickListener(this);
        gridView = (MyGridView) findViewById(R.id.mygridview);
        share = (LinearLayout) findViewById(R.id.retweet);
        share.setOnClickListener(this);
        commit = (LinearLayout) findViewById(R.id.comment);
        commit.setOnClickListener(this);
        like = (LinearLayout) findViewById(R.id.like);
        like.setOnClickListener(this);

        frome_text = (TextView) findViewById(R.id.frome_text);
        frome_status_layout = (LinearLayout) findViewById(R.id.frome_status);
        frome_grid = (MyGridView) findViewById(R.id.frome_grid);

        options = new DisplayImageOptions.Builder()
//                .showImageOnLoading(R.drawable.logo)
//                .showImageForEmptyUri(R.drawable.logo)
//                .showImageOnFail(R.drawable.logo)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .displayer(new FadeInBitmapDisplayer(100)) // 展现方式：渐现
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        initDate();
    }

    private void initDate() {
        name.setText(status.user.screen_name);
        time.setText(DateUtil.GmtToDatastring(status.created_at).substring(5, 16));
        frome.setText(Html.fromHtml(status.source).toString());
        text.setText(StringUtil.ToDBC(status.text));
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
            frome_text.setText(StringUtil.ToDBC(status.retweeted_status.text));
            StringUtil.setTextview(frome_text, this);
            if (status.retweeted_status.pic_urls == null) {
                frome_grid.setVisibility(View.GONE);
            } else {
                frome_grid.setVisibility(View.VISIBLE);
                adapter = new MyGridviewAdapter(status.retweeted_status.pic_urls, this);
                frome_grid.setAdapter(adapter);
            }
        }
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
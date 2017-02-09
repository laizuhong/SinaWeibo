package com.example.laizuhong.sinaweibo.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.laizuhong.sinaweibo.ImageActivity;
import com.example.laizuhong.sinaweibo.MyApp;
import com.example.laizuhong.sinaweibo.R;
import com.example.laizuhong.sinaweibo.UserActivity;
import com.example.laizuhong.sinaweibo.WeiboDetailActivity;
import com.example.laizuhong.sinaweibo.util.CatnutUtils;
import com.example.laizuhong.sinaweibo.util.DateUtil;
import com.example.laizuhong.sinaweibo.util.MyGridView;
import com.example.laizuhong.sinaweibo.util.MyLog;
import com.example.laizuhong.sinaweibo.util.TweetImageSpan;
import com.example.laizuhong.sinaweibo.util.TweetTextView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sina.weibo.sdk.openapi.models.Favorite;
import com.sina.weibo.sdk.openapi.models.Status;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by laizuhong on 2017/2/8.
 */

public class FavoriteRecyAdapter extends BaseQuickAdapter<Favorite>{

    private Context context;
    private List<Favorite> favorites;
    private TweetImageSpan tweetImageSpan;

    public FavoriteRecyAdapter(Context context, List<Favorite> data) {
        super(context, R.layout.item_weibo, data);
        this.context=context;
        this.favorites=data;
        tweetImageSpan = new TweetImageSpan(context);
    }

    @Override
    protected void convert(BaseViewHolder holer, final Favorite favorite) {
        Status status=favorite.status;
        holer.setText(R.id.username,status.user.screen_name);

        holer.setText(R.id.time,DateUtil.GmtToDatastring(status.created_at).substring(5, 16));

        holer.setText(R.id.frome,Html.fromHtml(status.source).toString());
        Log.e("frome", status.source);
        holer.setText(R.id.text,status.text);
        CatnutUtils.vividTweet((TweetTextView) holer.getView(R.id.text), tweetImageSpan);
//        StringUtil.setTextview(holer.text, context);


        int position=holer.getAdapterPosition();
        ImageView userhead=holer.getView(R.id.userhead);
        ImageLoader.getInstance().displayImage(status.user.avatar_large, userhead, MyApp.options);
        userhead.setTag(position);

        userhead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int p = (int) v.getTag();
                String name = favorites.get(p).status.user.screen_name;
                Intent intent = new Intent(context, UserActivity.class);
                intent.putExtra("name", name);
                context.startActivity(intent);

            }
        });

        holer.setVisible(R.id.grid1,false);
//        holer.grid2.setVisibility(View.GONE);
        holer.setVisible(R.id.mygridview,false);
        if (status.pic_urls != null) {
            if (status.pic_urls.size() == 1) {
                holer.setVisible(R.id.grid1,true);
                ImageLoader.getInstance().displayImage(status.pic_urls.get(0), (ImageView) holer.getView(R.id.grid1), MyApp.options);
                holer.setTag(R.id.grid1,status.pic_urls);
                holer.setOnClickListener(R.id.grid1,new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ArrayList list = (ArrayList) v.getTag();
                        Intent intent = new Intent(context, ImageActivity.class);
                        intent.putStringArrayListExtra("image", list);
                        intent.putExtra("positon", 0);
                        context.startActivity(intent);
                    }
                });
            } else {

                MyGridView gridView=holer.getView(R.id.mygridview);
                if (status.pic_urls.size()==4){
                    gridView.setNumColumns(2);
                }else {
                    gridView.setNumColumns(3);
                }
                gridView.setVisibility(View.VISIBLE);
                MyGridviewAdapter adapter = new MyGridviewAdapter(status.pic_urls, context, gridView);
                gridView.setAdapter(adapter);
                gridView.setOnTouchInvalidPositionListener(new MyGridView.OnTouchInvalidPositionListener() {
                    @Override
                    public boolean onTouchInvalidPosition(int motionEvent) {
                        MyLog.e(motionEvent + "");
                        return false;
                    }
                });

                gridView.setTag(position);
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        MyLog.e(position + "   " + parent.getTag());
                        int p= (int) parent.getTag();
                        Intent intent = new Intent(context, ImageActivity.class);
                        intent.putStringArrayListExtra("image", favorites.get(p).status.pic_urls);
                        intent.putExtra("positon", position);
                        context.startActivity(intent);
                    }
                });
            }
        } else {
            holer.setVisible(R.id.mygridview,false);
        }


        if (status.retweeted_status != null) {
            MyLog.e(status.retweeted_status.toString());

            holer.setVisible(R.id.frome_status,true);
            holer.setTag(R.id.frome_status,position);
            holer.setOnClickListener(R.id.frome_status,new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int p = (int) v.getTag();
                    Intent intent = new Intent(context, WeiboDetailActivity.class);
                    intent.putExtra("weibo", favorites.get(p).status.retweeted_status);
                    context.startActivity(intent);
                }
            });
            String text;
            if (status.retweeted_status.user==null){
                text=status.retweeted_status.text;
            }else {
                text = status.retweeted_status.user.screen_name + ":  " + status.retweeted_status.text;
            }
            holer.setText(R.id.item_frome_text,text);
            CatnutUtils.vividTweet((TweetTextView) holer.getView(R.id.item_frome_text), tweetImageSpan);
            // StringUtil.setTextview(holer.frome_text, context);
            if (status.retweeted_status.pic_urls != null) {
                MyGridView gridView=holer.getView(R.id.frome_grid);
                gridView.setVisibility(View.VISIBLE);
                MyGridviewAdapter adapter = new MyGridviewAdapter(status.retweeted_status.pic_urls, context, gridView);
                gridView.setAdapter(adapter);
                gridView.setOnTouchInvalidPositionListener(new MyGridView.OnTouchInvalidPositionListener() {
                    @Override
                    public boolean onTouchInvalidPosition(int motionEvent) {
                        MyLog.e(motionEvent + "");
                        return false;
                    }
                });

                gridView.setTag(position);
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        MyLog.e(position + "");
                        int p= (int) parent.getTag();
                        Intent intent = new Intent(context, ImageActivity.class);
                        intent.putStringArrayListExtra("image", favorites.get(p).status.retweeted_status.pic_urls);
                        intent.putExtra("positon", position);
                        context.startActivity(intent);
                    }
                });
            } else {
                holer.setVisible(R.id.frome_grid,false);
            }
        } else {
            holer.setVisible(R.id.frome_status,false);
        }

        if (status.reposts_count != 0) {
            holer.setVisible(R.id.share_layout,true);
            holer.setText(R.id.share,status.reposts_count + "");
        } else {
            holer.setVisible(R.id.share_layout,false);
        }
        if (status.comments_count != 0) {
            holer.setVisible(R.id.comment_layout,true);
            holer.setText(R.id.comment,status.comments_count + "");
        } else {
            holer.setVisible(R.id.comment_layout,false);
        }
        holer.setTag(R.id.item_list,position);
        holer.setOnClickListener(R.id.item_list,new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int p = (int) v.getTag();
                Intent intent = new Intent(context, WeiboDetailActivity.class);
                intent.putExtra("weibo", favorites.get(p).status);
                context.startActivity(intent);
            }
        });
    }
}

package com.example.laizuhong.sinaweibo.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.laizuhong.sinaweibo.R;
import com.example.laizuhong.sinaweibo.SendWeiboActivity;
import com.example.laizuhong.sinaweibo.UserWeiboActivity;
import com.example.laizuhong.sinaweibo.WeiboDetailActivity;
import com.example.laizuhong.sinaweibo.util.CatnutUtils;
import com.example.laizuhong.sinaweibo.util.DateUtil;
import com.example.laizuhong.sinaweibo.util.MyGridView;
import com.example.laizuhong.sinaweibo.util.MyLog;
import com.example.laizuhong.sinaweibo.util.TweetImageSpan;
import com.example.laizuhong.sinaweibo.util.TweetTextView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.sina.weibo.sdk.openapi.models.Status;

import java.util.List;

/**
 * Created by laizuhong on 2015/9/16.
 */
public class WeiboAdapter extends BaseAdapter{


    List<Status> statuses;
    Context context;
    int MODE = 0;
    DisplayImageOptions options;
    TweetImageSpan tweetImageSpan;

    public WeiboAdapter(Context context,List<Status> statuses,int mode){
        this.context=context;
        this.statuses=statuses;
        this.MODE=mode;
        tweetImageSpan = new TweetImageSpan(context);
        options = new DisplayImageOptions.Builder()
//                .showImageOnLoading(R.drawable.logo)
//                .showImageForEmptyUri(R.drawable.logo)
//                .showImageOnFail(R.drawable.logo)
                .cacheInMemory(true)
                .cacheOnDisk(true)
//                .displayer(new FadeInBitmapDisplayer(100)) // 展现方式：渐现
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

    }



    @Override
    public int getCount() {
        return statuses.size();
    }

    @Override
    public Object getItem(int position) {
        return statuses.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHoler holer;
        if (convertView==null){
            holer=new ViewHoler();
            convertView= LayoutInflater.from(context).inflate(R.layout.item_weibo,null);
            holer.username= (TextView) convertView.findViewById(R.id.username);
            holer.time= (TextView) convertView.findViewById(R.id.time);
            holer.from = (TextView) convertView.findViewById(R.id.frome);
            holer.text = (TweetTextView) convertView.findViewById(R.id.text);
            holer.sharecount= (TextView) convertView.findViewById(R.id.retweetcount);
            holer.commentcount= (TextView) convertView.findViewById(R.id.commentcount);
            holer.likecount= (TextView) convertView.findViewById(R.id.likecount);
            holer.share= (LinearLayout) convertView.findViewById(R.id.retweet);
            holer.comment= (LinearLayout) convertView.findViewById(R.id.comment);
            holer.like= (LinearLayout) convertView.findViewById(R.id.like);
            holer.likeImage= (ImageView) convertView.findViewById(R.id.likeimage);
            holer.userhead= (ImageView) convertView.findViewById(R.id.userhead);
            holer.gridView = (MyGridView) convertView.findViewById(R.id.mygridview);
            holer.frome_status= (LinearLayout) convertView.findViewById(R.id.frome_status);
            holer.frome_text = (TweetTextView) convertView.findViewById(R.id.frome_text);
            holer.frome_grid = (MyGridView) convertView.findViewById(R.id.frome_grid);
            holer.item_list = (LinearLayout) convertView.findViewById(R.id.item_list);
            convertView.setTag(holer);
        }else {
            holer= (ViewHoler) convertView.getTag();
        }
        final Status status = statuses.get(position);
        holer.username.setText(status.user.screen_name);

        holer.time.setText(DateUtil.GmtToDatastring(status.created_at).substring(5, 16));

        holer.from.setText(Html.fromHtml(status.source).toString());
        Log.e("frome", status.source);
        holer.text.setText(status.text);
        CatnutUtils.vividTweet(holer.text, tweetImageSpan);


        // StringUtil.setTextview(holer.text, context);
        com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(status.user.profile_image_url, holer.userhead, options);
        holer.userhead.setTag(position);
        holer.userhead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int p = (int) v.getTag();
                String uid = statuses.get(p).user.id;
                String name = statuses.get(p).user.screen_name;
                Intent intent = new Intent(context, UserWeiboActivity.class);
                intent.putExtra("uid", uid);
                intent.putExtra("name", name);
                context.startActivity(intent);

            }
        });
        if (status.pic_urls!=null){
            holer.gridView.setVisibility(View.VISIBLE);
            MyGridviewAdapter adapter = new MyGridviewAdapter(status.pic_urls, context);
            holer.gridView.setAdapter(adapter);
            holer.gridView.setOnTouchInvalidPositionListener(new MyGridView.OnTouchInvalidPositionListener() {
                @Override
                public boolean onTouchInvalidPosition(int motionEvent) {
                    MyLog.e(motionEvent + "");
                    return false;
                }
            });
            holer.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    MyLog.e(position + "");
                }
            });
        }else {
            holer.gridView.setVisibility(View.GONE);
        }


        if (status.retweeted_status!=null){
            holer.frome_status.setVisibility(View.VISIBLE);
            holer.frome_status.setTag(position);
            holer.frome_status.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int p = (int) v.getTag();
                    Intent intent = new Intent(context, WeiboDetailActivity.class);
                    intent.putExtra("weibo", statuses.get(p).retweeted_status);
                    context.startActivity(intent);
                }
            });
            String text=status.retweeted_status.user.screen_name+":  "+status.retweeted_status.text;
            holer.frome_text.setText(text);
            CatnutUtils.vividTweet(holer.frome_text, tweetImageSpan);
            // StringUtil.setTextview(holer.frome_text, context);
            if (status.retweeted_status.pic_urls!=null){
                holer.frome_grid.setVisibility(View.VISIBLE);
                MyGridviewAdapter adapter = new MyGridviewAdapter(status.retweeted_status.pic_urls, context);
                holer.frome_grid.setAdapter(adapter);
            }else {
                holer.frome_grid.setVisibility(View.GONE);
            }
        }else {
            holer.frome_status.setVisibility(View.GONE);
        }

        if (status.reposts_count==0){
            holer.sharecount.setText("转发");
        }else {
            holer.sharecount.setText(status.reposts_count + "");
        }
        if (status.comments_count==0){
            holer.commentcount.setText("评论");
        }else {
            holer.commentcount.setText(status.comments_count+"");
        }
        if (status.attitudes_count==0){
            holer.likecount.setText("赞");
        }else {
            holer.likecount.setText(status.attitudes_count+"");
        }

        holer.share.setTag(position);
        holer.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int p = (int) v.getTag();
                Intent intent = new Intent(context, SendWeiboActivity.class);
                intent.putExtra("weibo", statuses.get(p));
                context.startActivity(intent);
            }
        });
        holer.comment.setTag(position);
        holer.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int p = (int) v.getTag();
                Intent intent = new Intent(context, WeiboDetailActivity.class);
                intent.putExtra("weibo", statuses.get(p));
                context.startActivity(intent);
            }
        });
        holer.item_list.setTag(position);
        holer.item_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int p = (int) v.getTag();
                Intent intent = new Intent(context, WeiboDetailActivity.class);
                intent.putExtra("weibo", statuses.get(p));
                context.startActivity(intent);
            }
        });
        return convertView;
    }



    class ViewHoler {
        TweetTextView text, frome_text;
        TextView username, time, sharecount, commentcount, likecount;
        LinearLayout share, comment, like, frome_status, item_list;
        ImageView likeImage, userhead;
        MyGridView gridView, frome_grid;
        TextView from;

    }







}

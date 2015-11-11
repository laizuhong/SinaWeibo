package com.example.laizuhong.sinaweibo.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bm.library.Info;
import com.example.laizuhong.sinaweibo.ImageActivity;
import com.example.laizuhong.sinaweibo.MyApp;
import com.example.laizuhong.sinaweibo.R;
import com.example.laizuhong.sinaweibo.UserWeiboActivity;
import com.example.laizuhong.sinaweibo.WeiboDetailActivity;
import com.example.laizuhong.sinaweibo.util.CatnutUtils;
import com.example.laizuhong.sinaweibo.util.DateUtil;
import com.example.laizuhong.sinaweibo.util.MyGridView;
import com.example.laizuhong.sinaweibo.util.MyLog;
import com.example.laizuhong.sinaweibo.util.TweetImageSpan;
import com.example.laizuhong.sinaweibo.util.TweetTextView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sina.weibo.sdk.openapi.models.Status;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by laizuhong on 2015/9/16.
 */
public class WeiboAdapter extends BaseAdapter{


    List<Status> statuses;
    Context context;
    int MODE = 0;
    TweetImageSpan tweetImageSpan;
    Info mInfo;

    AlphaAnimation in = new AlphaAnimation(0, 1);
    AlphaAnimation out = new AlphaAnimation(1, 0);

    public WeiboAdapter(Context context,List<Status> statuses,int mode){
        this.context=context;
        this.statuses=statuses;
        this.MODE=mode;
        tweetImageSpan = new TweetImageSpan(context);
        in.setDuration(300);
        out.setDuration(300);
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
            holer.sharecount = (TextView) convertView.findViewById(R.id.share);
            holer.commentcount = (TextView) convertView.findViewById(R.id.comment);
            holer.share_layout = (LinearLayout) convertView.findViewById(R.id.share_layout);
            holer.comment_layout = (LinearLayout) convertView.findViewById(R.id.comment_layout);
            holer.likeImage= (ImageView) convertView.findViewById(R.id.likeimage);
            holer.userhead= (ImageView) convertView.findViewById(R.id.userhead);
            holer.gridView = (MyGridView) convertView.findViewById(R.id.mygridview);
            holer.grid1 = (ImageView) convertView.findViewById(R.id.grid1);
            holer.grid2 = (MyGridView) convertView.findViewById(R.id.grid2);
            holer.frome_status= (LinearLayout) convertView.findViewById(R.id.frome_status);
            holer.frome_text = (TweetTextView) convertView.findViewById(R.id.frome_text);
            holer.frome_grid = (MyGridView) convertView.findViewById(R.id.frome_grid);
            holer.item_list = (LinearLayout) convertView.findViewById(R.id.item_list);
            convertView.setTag(holer);
        }else {
            holer= (ViewHoler) convertView.getTag();
        }
        final Status status = statuses.get(position);
        MyLog.e(status.bmiddle_pic + "   " + status.thumbnail_pic + "   " + status.original_pic);
        holer.username.setText(status.user.screen_name);

        holer.time.setText(DateUtil.GmtToDatastring(status.created_at).substring(5, 16));

        holer.from.setText(Html.fromHtml(status.source).toString());
        Log.e("frome", status.source);
        holer.text.setText(status.text);
        CatnutUtils.vividTweet(holer.text, tweetImageSpan);
//        StringUtil.setTextview(holer.text, context);



        ImageLoader.getInstance().displayImage(status.user.avatar_large, holer.userhead, MyApp.options);
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
        holer.grid1.setVisibility(View.GONE);
        holer.grid2.setVisibility(View.GONE);
        holer.gridView.setVisibility(View.GONE);
        if (status.pic_urls!=null){
            if (status.pic_urls.size() == 1) {
                holer.grid1.setVisibility(View.VISIBLE);
                ImageLoader.getInstance().displayImage(status.pic_urls.get(0), holer.grid1, MyApp.options);
                holer.grid1.setTag(status.pic_urls);
                holer.grid1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ArrayList list = (ArrayList) v.getTag();
                        Intent intent = new Intent(context, ImageActivity.class);
                        intent.putStringArrayListExtra("image", list);
                        intent.putExtra("positon", 0);
                        context.startActivity(intent);
                    }
                });
            } else if (status.pic_urls.size() == 4) {
                holer.grid2.setVisibility(View.VISIBLE);
                MyGridviewAdapter adapter = new MyGridviewAdapter(status.pic_urls, context, holer.grid2);
                holer.grid2.setAdapter(adapter);
                holer.grid2.setOnTouchInvalidPositionListener(new MyGridView.OnTouchInvalidPositionListener() {
                    @Override
                    public boolean onTouchInvalidPosition(int motionEvent) {
                        MyLog.e(motionEvent + "");
                        return false;
                    }
                });

                holer.grid2.setTag(position);
                holer.grid2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        MyLog.e(position + "   " + view.getTag());
                        //int p= (int) view.getTag();
                        Intent intent = new Intent(context, ImageActivity.class);
                        intent.putStringArrayListExtra("image", status.pic_urls);
                        intent.putExtra("positon", position);
                        context.startActivity(intent);
                    }
                });
            } else {

                holer.gridView.setVisibility(View.VISIBLE);
                MyGridviewAdapter adapter = new MyGridviewAdapter(status.pic_urls, context, holer.gridView);
                holer.gridView.setAdapter(adapter);
                holer.gridView.setOnTouchInvalidPositionListener(new MyGridView.OnTouchInvalidPositionListener() {
                    @Override
                    public boolean onTouchInvalidPosition(int motionEvent) {
                        MyLog.e(motionEvent + "");
                        return false;
                    }
                });

                holer.gridView.setTag(position);
                holer.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        MyLog.e(position + "   " + view.getTag());
                        //int p= (int) view.getTag();
                        Intent intent = new Intent(context, ImageActivity.class);
                        intent.putStringArrayListExtra("image", status.pic_urls);
                        intent.putExtra("positon", position);
                        context.startActivity(intent);
                    }
                });
            }
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
                MyGridviewAdapter adapter = new MyGridviewAdapter(status.retweeted_status.pic_urls, context, holer.frome_grid);
                holer.frome_grid.setAdapter(adapter);
                holer.frome_grid.setOnTouchInvalidPositionListener(new MyGridView.OnTouchInvalidPositionListener() {
                    @Override
                    public boolean onTouchInvalidPosition(int motionEvent) {
                        MyLog.e(motionEvent + "");
                        return false;
                    }
                });
                holer.frome_grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        MyLog.e(position + "");
                        Intent intent = new Intent(context, ImageActivity.class);
                        intent.putStringArrayListExtra("image", status.retweeted_status.pic_urls);
                        intent.putExtra("positon", position);
                        context.startActivity(intent);
                    }
                });
            }else {
                holer.frome_grid.setVisibility(View.GONE);
            }
        }else {
            holer.frome_status.setVisibility(View.GONE);
        }

        if (status.reposts_count != 0) {
            holer.share_layout.setVisibility(View.VISIBLE);
            holer.sharecount.setText(status.reposts_count + "");
        }else {
            holer.share_layout.setVisibility(View.GONE);
        }
        if (status.comments_count != 0) {
            holer.comment_layout.setVisibility(View.VISIBLE);
            holer.commentcount.setText(status.comments_count+"");
        }else {
            holer.comment_layout.setVisibility(View.GONE);
        }
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
        TextView username, time, sharecount, commentcount;
        LinearLayout frome_status, item_list, share_layout, comment_layout;
        ImageView likeImage, userhead, grid1;
        MyGridView gridView, frome_grid, grid2;
        TextView from;

    }







}

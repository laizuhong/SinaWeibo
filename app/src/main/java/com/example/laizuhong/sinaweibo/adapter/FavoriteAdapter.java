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
import com.example.laizuhong.sinaweibo.UserActivity;
import com.example.laizuhong.sinaweibo.WeiboDetailActivity;
import com.example.laizuhong.sinaweibo.util.CatnutUtils;
import com.example.laizuhong.sinaweibo.util.DateUtil;
import com.example.laizuhong.sinaweibo.util.MyGridView;
import com.example.laizuhong.sinaweibo.util.MyLog;
import com.example.laizuhong.sinaweibo.util.TweetImageSpan;
import com.example.laizuhong.sinaweibo.util.TweetTextView;
import com.example.laizuhong.sinaweibo.util.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sina.weibo.sdk.openapi.models.Favorite;
import com.sina.weibo.sdk.openapi.models.Status;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by laizuhong on 2015/11/12.
 */
public class FavoriteAdapter extends BaseAdapter {

    List<Favorite> statuses;
    Context context;
    int MODE = 0;
    TweetImageSpan tweetImageSpan;
    Info mInfo;

    AlphaAnimation in = new AlphaAnimation(0, 1);
    AlphaAnimation out = new AlphaAnimation(1, 0);

    public FavoriteAdapter(Context context, List<Favorite> statuses, int mode) {
        this.context = context;
        this.statuses = statuses;
        this.MODE = mode;
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
        ViewHolder holer;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_weibo, null);
            holer = new ViewHolder(convertView);
            convertView.setTag(holer);

        } else {
            holer = (ViewHolder) convertView.getTag();
        }
        final Status status = statuses.get(position).status;
        MyLog.e(status.bmiddle_pic + "   " + status.thumbnail_pic + "   " + status.original_pic + "   " + status.user.screen_name);
        holer.username.setText(status.user.screen_name);

        holer.time.setText(DateUtil.GmtToDatastring(status.created_at).substring(5, 16));

        holer.frome.setText(Html.fromHtml(status.source).toString());
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
                String name = statuses.get(p).status.user.screen_name;
                Intent intent = new Intent(context, UserActivity.class);
                intent.putExtra("name", name);
                context.startActivity(intent);

            }
        });
        holer.grid1.setVisibility(View.GONE);
        holer.grid2.setVisibility(View.GONE);
        holer.mygridview.setVisibility(View.GONE);
        if (status.pic_urls != null) {
            if (status.pic_urls.size() == 1) {
                holer.grid1.setVisibility(View.VISIBLE);
                MyLog.e("pic_url.size==1", status.pic_urls.get(0));
                ImageLoader.getInstance().displayImage(Utils.getUrl(status.pic_urls.get(0)), holer.grid1, MyApp.options);
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

                holer.mygridview.setVisibility(View.VISIBLE);
                MyGridviewAdapter adapter = new MyGridviewAdapter(status.pic_urls, context, holer.mygridview);
                holer.mygridview.setAdapter(adapter);
                holer.mygridview.setOnTouchInvalidPositionListener(new MyGridView.OnTouchInvalidPositionListener() {
                    @Override
                    public boolean onTouchInvalidPosition(int motionEvent) {
                        MyLog.e(motionEvent + "");
                        return false;
                    }
                });

                holer.mygridview.setTag(position);
                holer.mygridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
        } else {
            holer.mygridview.setVisibility(View.GONE);
        }


        if (status.retweeted_status != null) {
            holer.fromeStatus.setVisibility(View.VISIBLE);
            holer.fromeStatus.setTag(position);
            holer.fromeStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int p = (int) v.getTag();
                    Intent intent = new Intent(context, WeiboDetailActivity.class);
                    intent.putExtra("weibo", statuses.get(p).status.retweeted_status);
                    context.startActivity(intent);
                }
            });
            String text = status.retweeted_status.user.screen_name + ":  " + status.retweeted_status.text;
            holer.itemFromeText.setText(text);
            CatnutUtils.vividTweet(holer.itemFromeText, tweetImageSpan);
            // StringUtil.setTextview(holer.frome_text, context);
            if (status.retweeted_status.pic_urls != null) {
                holer.fromeGrid.setVisibility(View.VISIBLE);
                MyGridviewAdapter adapter = new MyGridviewAdapter(status.retweeted_status.pic_urls, context, holer.fromeGrid);
                holer.fromeGrid.setAdapter(adapter);
                holer.fromeGrid.setOnTouchInvalidPositionListener(new MyGridView.OnTouchInvalidPositionListener() {
                    @Override
                    public boolean onTouchInvalidPosition(int motionEvent) {
                        MyLog.e(motionEvent + "");
                        return false;
                    }
                });
                holer.fromeGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        MyLog.e(position + "");
                        Intent intent = new Intent(context, ImageActivity.class);
                        intent.putStringArrayListExtra("image", status.retweeted_status.pic_urls);
                        intent.putExtra("positon", position);
                        context.startActivity(intent);
                    }
                });
            } else {
                holer.fromeGrid.setVisibility(View.GONE);
            }
        } else {
            holer.fromeStatus.setVisibility(View.GONE);
        }

        if (status.reposts_count != 0) {
            holer.shareLayout.setVisibility(View.VISIBLE);
            holer.share.setText(status.reposts_count + "");
        } else {
            holer.shareLayout.setVisibility(View.GONE);
        }
        if (status.comments_count != 0) {
            holer.commentLayout.setVisibility(View.VISIBLE);
            holer.comment.setText(status.comments_count + "");
        } else {
            holer.commentLayout.setVisibility(View.GONE);
        }
        holer.itemList.setTag(position);
        holer.itemList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int p = (int) v.getTag();
                Intent intent = new Intent(context, WeiboDetailActivity.class);
                intent.putExtra("weibo", statuses.get(p).status);
                context.startActivity(intent);
            }
        });
        return convertView;
    }


    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'item_weibo.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */
    static class ViewHolder {
        @Bind(R.id.userhead)
        ImageView userhead;
        @Bind(R.id.username)
        TextView username;
        @Bind(R.id.time)
        TextView time;
        @Bind(R.id.frome)
        TextView frome;
        @Bind(R.id.share)
        TextView share;
        @Bind(R.id.share_layout)
        LinearLayout shareLayout;
        @Bind(R.id.comment)
        TextView comment;
        @Bind(R.id.comment_layout)
        LinearLayout commentLayout;
        @Bind(R.id.text)
        TweetTextView text;
        @Bind(R.id.grid1)
        ImageView grid1;
        @Bind(R.id.mygridview)
        MyGridView mygridview;
        @Bind(R.id.grid2)
        MyGridView grid2;
        @Bind(R.id.item_list)
        LinearLayout itemList;
        @Bind(R.id.item_frome_text)
        TweetTextView itemFromeText;
        @Bind(R.id.frome_grid)
        MyGridView fromeGrid;
        @Bind(R.id.frome_status)
        LinearLayout fromeStatus;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}

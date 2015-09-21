package com.example.laizuhong.sinaweibo.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.laizuhong.sinaweibo.R;
import com.example.laizuhong.sinaweibo.UserWeiboActivity;
import com.example.laizuhong.sinaweibo.util.DateUtil;
import com.example.laizuhong.sinaweibo.util.OnTextviewClickListener;
import com.example.laizuhong.sinaweibo.util.WeiboClickSpan;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.sina.weibo.sdk.openapi.models.Status;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by laizuhong on 2015/9/16.
 */
public class WeiboAdapter extends BaseAdapter{

    List<Status> statuses;
    Context context;
    int MODE=0;
    private static  final  Pattern itemuser=Pattern.compile("@\\w+ ");
    private static  final  Pattern web1=Pattern.compile("http://\\w+");
    private static  final  Pattern web2=Pattern.compile("#\\w+#");
    DisplayImageOptions options;

    public WeiboAdapter(Context context,List<Status> statuses,int mode){
        this.context=context;
        this.statuses=statuses;
        this.MODE=mode;
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.logo)
                .showImageForEmptyUri(R.drawable.logo)
                .showImageOnFail(R.drawable.logo)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .displayer(new FadeInBitmapDisplayer(100)) // 展现方式：渐现
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHoler holer;
        if (convertView==null){
            holer=new ViewHoler();
            convertView= LayoutInflater.from(context).inflate(R.layout.item_weibo,null);
            holer.username= (TextView) convertView.findViewById(R.id.username);
            holer.time= (TextView) convertView.findViewById(R.id.time);
            holer.frome= (TextView) convertView.findViewById(R.id.frome);
            holer.text= (TextView) convertView.findViewById(R.id.text);
            holer.sharecount= (TextView) convertView.findViewById(R.id.retweetcount);
            holer.commentcount= (TextView) convertView.findViewById(R.id.commentcount);
            holer.likecount= (TextView) convertView.findViewById(R.id.likecount);
            holer.share= (LinearLayout) convertView.findViewById(R.id.retweet);
            holer.comment= (LinearLayout) convertView.findViewById(R.id.comment);
            holer.like= (LinearLayout) convertView.findViewById(R.id.like);
            holer.likeImage= (ImageView) convertView.findViewById(R.id.likeimage);
            holer.userhead= (ImageView) convertView.findViewById(R.id.userhead);
            holer.gridView= (GridView) convertView.findViewById(R.id.mygridview);
            holer.frome_status= (LinearLayout) convertView.findViewById(R.id.frome_status);
            holer.frome_text= (TextView) convertView.findViewById(R.id.frome_text);
            holer.frome_grid= (GridView) convertView.findViewById(R.id.frome_grid);
            convertView.setTag(holer);
        }else {
            holer= (ViewHoler) convertView.getTag();
        }
        Status status=statuses.get(position);
        holer.username.setText(status.user.screen_name);

        holer.time.setText(DateUtil.GmtToDatastring(status.created_at));
        if (status.source.contains("weibo")) {
            holer.frome.setText("微博");
        }
        holer.text.setText(ToDBC(status.text));
       setTextview(holer.text);
        com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(status.user.profile_image_url, holer.userhead, options);
        if (status.pic_urls!=null){
            holer.gridView.setVisibility(View.VISIBLE);
            MyGridviewAdapter  adapter=new MyGridviewAdapter(status.pic_urls);
            holer.gridView.setAdapter(adapter);
        }else {
            holer.gridView.setVisibility(View.GONE);
        }


        if (status.retweeted_status!=null){
            holer.frome_status.setVisibility(View.VISIBLE);
            String text=status.retweeted_status.user.screen_name+":  "+status.retweeted_status.text;
            holer.frome_text.setText(ToDBC(text));
            setTextview(holer.frome_text);
            if (status.retweeted_status.pic_urls!=null){
                holer.frome_grid.setVisibility(View.VISIBLE);
                MyGridviewAdapter adapter=new MyGridviewAdapter(status.retweeted_status.pic_urls);
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

        return convertView;
    }


    class ViewHoler{
        TextView username,time,frome,text,frome_text,sharecount,commentcount,likecount;
        LinearLayout share,comment,like,frome_status;
        ImageView likeImage,userhead;
        GridView gridView,frome_grid;
    }


    /**
     * 半角转换为全角
     *
     * @param input
     * @return
     */
    public static String ToDBC(String input) {
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 12288) {
                c[i] = (char) 32;
                continue;
            }
            if (c[i] > 65280 && c[i] < 65375)
                c[i] = (char) (c[i] - 65248);
        }
        return new String(c);
    }



    class MyGridviewAdapter extends  BaseAdapter{



        ArrayList<String> pic;

        public MyGridviewAdapter(ArrayList<String> pic) {
            this.pic = pic;
        }



        @Override
        public int getCount() {
            if (pic.size()>9){
                return 9;
            }
            return pic.size();
        }

        @Override
        public Object getItem(int position) {
            return pic.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView=LayoutInflater.from(context).inflate(R.layout.item_gridview,null);

            ImageView imageView= (ImageView) convertView.findViewById(R.id.image);
           // imageView.setLayoutParams(new ViewGroup.LayoutParams(DisplayUtil.px2dip(context,MyApp.width/3-20),DisplayUtil.px2dip(context,MyApp.width/3-20)));
           // Log.e("image_url",pic.get(position));
            com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(pic.get(position),imageView,options);
            return convertView;
        }
    }

    private void setTextview(TextView textview){
        String string=textview.getText().toString();
        SpannableString spannableString=new SpannableString(string);
        setKeyworkClickable(textview,spannableString,itemuser,new WeiboClickSpan(new OnTextviewClickListener() {
            @Override
            public void clickTextView() {
                Intent intent=new Intent(context, UserWeiboActivity.class);
                context.startActivity(intent);
            }

            @Override
            public void setStyle(TextPaint ds) {
                ds.setColor(context.getResources().getColor(R.color.green));
                ds.setUnderlineText(false);
            }
        }));
        setKeyworkClickable(textview,spannableString,web1,new WeiboClickSpan(new OnTextviewClickListener() {
            @Override
            public void clickTextView() {
                Intent intent=new Intent(context, UserWeiboActivity.class);
                context.startActivity(intent);
            }

            @Override
            public void setStyle(TextPaint ds) {
                ds.setColor(context.getResources().getColor(R.color.green));
                ds.setUnderlineText(false);
            }
        }));
        setKeyworkClickable(textview,spannableString,web2,new WeiboClickSpan(new OnTextviewClickListener() {
            @Override
            public void clickTextView() {
                Intent intent=new Intent(context, UserWeiboActivity.class);
                context.startActivity(intent);
            }

            @Override
            public void setStyle(TextPaint ds) {
                ds.setColor(context.getResources().getColor(R.color.green));
                ds.setUnderlineText(false);
            }
        }));
    }



    /*
     *设置具体哪个关键字可点击
     */
    private void setKeyworkClickable(TextView textView,SpannableString spannableString,Pattern pattern,ClickableSpan clickableSpan){
        Matcher matcher=pattern.matcher(spannableString.toString());
        while (matcher.find()){
            String key=matcher.group();
            if (!"".equals(key)){
                int start =spannableString.toString().indexOf(key);
                int end =start+key.length();
                setClickTextview(textView,spannableString,start,end,clickableSpan);
            }
        }
    }

    /*
     *设置textview中的字段可点击
     */
    private void setClickTextview(TextView textview,SpannableString spannableString,int start,int end ,ClickableSpan clickableSpan){
        spannableString.setSpan(clickableSpan,start,end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textview.setText(spannableString);
        textview.setMovementMethod(LinkMovementMethod.getInstance());
    }

}

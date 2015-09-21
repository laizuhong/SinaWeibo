package com.example.laizuhong.sinaweibo.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.laizuhong.sinaweibo.R;
import com.example.laizuhong.sinaweibo.SettingActivity;
import com.example.laizuhong.sinaweibo.config.AccessTokenKeeper;
import com.example.laizuhong.sinaweibo.config.Constants;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.UsersAPI;
import com.sina.weibo.sdk.openapi.models.ErrorInfo;
import com.sina.weibo.sdk.openapi.models.User;
import com.sina.weibo.sdk.utils.LogUtil;

/**
 * Created by laizuhong on 2015/9/16.
 */
public class MineFragment extends Fragment implements View.OnClickListener{

    private View rootView;//缓存Fragment view
    ImageView userhead;
    TextView userName,introduction,myweibocount,myfocuscount,myfanscount,weibolevel;
    /** 当前 Token 信息 */
    private Oauth2AccessToken mAccessToken;
    /** 用户信息接口 */
    private UsersAPI mUsersAPI;
    User user;
    DisplayImageOptions options;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(rootView==null){
            rootView=inflater.inflate(R.layout.fragment_mine, null);
        }
        //缓存的rootView需要判断是否已经被加过parent， 如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }
        init(rootView);
        return rootView;
    }


    private void init(View v){

        mAccessToken= AccessTokenKeeper.readAccessToken(getActivity());
        userhead= (ImageView) v.findViewById(R.id.userhead);
        userName= (TextView) v.findViewById(R.id.username);
        introduction= (TextView) v.findViewById(R.id.jianjie);
        myweibocount= (TextView) v.findViewById(R.id.myweibocount);
        myfocuscount= (TextView) v.findViewById(R.id.myfocuscount);
        myfanscount= (TextView) v.findViewById(R.id.myfanscount);
        v.findViewById(R.id.user_set).setOnClickListener(this);
        v.findViewById(R.id.user).setOnClickListener(this);
        v.findViewById(R.id.weibo).setOnClickListener(this);
        v.findViewById(R.id.focus).setOnClickListener(this);
        v.findViewById(R.id.fans).setOnClickListener(this);
        v.findViewById(R.id.my_new_friend).setOnClickListener(this);
        mUsersAPI = new UsersAPI(getActivity(), Constants.APP_KEY, mAccessToken);
        long uid = Long.parseLong(mAccessToken.getUid());
        Log.e("uid",uid+"   123");
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

        mUsersAPI.show(uid, mListener);
    }


    @Override
    public void onStart() {

        super.onStart();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.user_set:
                Intent set=new Intent(getActivity(), SettingActivity.class);
                startActivity(set);
                break;
            case R.id.user:
                break;
            case R.id.weibo:
                break;
            case R.id.focus:
                break;
            case R.id.fans:
                break;
            case R.id.my_new_friend:
                break;
        }
    }


    /**
     * 微博 OpenAPI 回调接口。
     */
    private RequestListener mListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                LogUtil.i("RequestListener user", response);
                // 调用 User#parse 将JSON串解析成User对象
                user = User.parse(response);
                if (user != null) {
                    userName.setText(user.screen_name);
                    introduction.setText("简介："+user.description);
                    myweibocount.setText(user.statuses_count+"");
                    myfocuscount.setText(user.friends_count+"");
                    myfanscount.setText(user.followers_count + "");
                    com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(user.avatar_large, userhead, options);
//                    userhead.setImageUrl(user.avatar_large,imageLoader);
                    Toast.makeText(getActivity(),
                            "获取User信息成功，用户昵称：" + user.screen_name,
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();
                }
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            LogUtil.e("RequestListener user", e.getMessage());
            ErrorInfo info = ErrorInfo.parse(e.getMessage());
            Toast.makeText(getActivity(), info.toString(), Toast.LENGTH_LONG).show();
        }
    };

}

package com.example.laizuhong.sinaweibo.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.laizuhong.sinaweibo.R;
import com.example.laizuhong.sinaweibo.UserActivity;
import com.example.laizuhong.sinaweibo.config.AccessTokenKeeper;
import com.example.laizuhong.sinaweibo.config.Constants;
import com.example.laizuhong.sinaweibo.util.DateUtil;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.UsersAPI;
import com.sina.weibo.sdk.openapi.models.ErrorInfo;
import com.sina.weibo.sdk.openapi.models.User;
import com.sina.weibo.sdk.utils.LogUtil;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by laizuhong on 2015/11/10.
 */
public class UserFragment extends Fragment {


    Context context;
    UsersAPI usersAPI;
    String username;
    User user;
    @Bind(R.id.name)
    TextView name;
    @Bind(R.id.sex)
    TextView sex;
    @Bind(R.id.location)
    TextView location;
    @Bind(R.id.level)
    TextView level;
    @Bind(R.id.create)
    TextView create;
    @Bind(R.id.desc)
    TextView desc;

    /**
     * 当前 Token 信息
     */
    private Oauth2AccessToken mAccessToken;
    /**
     * 微博 OpenAPI 回调接口。
     */
    private RequestListener mListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                Log.e("RequestListener users", response);
                user = User.parse(response);
                if (user != null) {
                    ((UserActivity) getActivity()).initImg(user.avatar_large);
                    initDate();
                }
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            LogUtil.e("onWeiboException", e.getMessage());
            ErrorInfo info = ErrorInfo.parse(e.getMessage());
            Toast.makeText(context, info.toString(), Toast.LENGTH_LONG).show();
        }
    };


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, null);
        context = getActivity();
        init(view);
        ButterKnife.bind(this, view);
        return view;
    }


    private void init(View view) {
        mAccessToken = AccessTokenKeeper.readAccessToken(context);
        usersAPI = new UsersAPI(context, Constants.APP_KEY, mAccessToken);
        username = ((UserActivity) getActivity()).getName();
        usersAPI.show(username, mListener);


    }

    private void initDate() {
        name.setText(user.screen_name);
        if (user.gender.equals("f")) {
            sex.setText("女");
        } else if (user.gender.equals("m")) {
            sex.setText("男");
        } else {
            sex.setText("未知");
        }
        location.setText(user.location);
        desc.setText(user.description);
        create.setText(DateUtil.GmtToDatastring(user.created_at).substring(0, 10));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}

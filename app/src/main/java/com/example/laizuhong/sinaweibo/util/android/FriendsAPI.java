package com.example.laizuhong.sinaweibo.util.android;

import android.content.Context;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.net.WeiboParameters;
import com.sina.weibo.sdk.openapi.AbsOpenAPI;

/**
 * Created by laizuhong on 2015/10/30.
 */
public class FriendsAPI extends AbsOpenAPI {

    /**
     * 构造函数，使用各个 API 接口提供的服务前必须先获取 Token。
     *
     * @param context
     * @param appKey
     * @param accessToken
     */
    public FriendsAPI(Context context, String appKey, Oauth2AccessToken accessToken) {
        super(context, appKey, accessToken);
    }


    /*
    *根据用ID获取微博列表
    */
    public void getUserFriends(String uid, int count, int cursor, RequestListener listener) {
        WeiboParameters params = new WeiboParameters(mAppKey);
        params.put("uid", uid);
        params.put("count", count);
        params.put("cursor", cursor);
        requestAsync("https://api.weibo.com/2/friendships/friends.json", params, HTTPMETHOD_GET, listener);
    }

}
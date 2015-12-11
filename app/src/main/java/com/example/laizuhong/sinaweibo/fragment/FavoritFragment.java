package com.example.laizuhong.sinaweibo.fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.example.laizuhong.sinaweibo.Constants;
import com.example.laizuhong.sinaweibo.Main;
import com.example.laizuhong.sinaweibo.adapter.FavoriteAdapter;
import com.sina.weibo.sdk.openapi.legacy.FavoritesAPI;
import com.sina.weibo.sdk.openapi.models.Favorite;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by laizuhong on 2015/11/12.
 */
public class FavoritFragment extends BaseFragment{

    int MODE;
    boolean fresh = false;
    FavoriteAdapter adapter;
    ArrayList<Favorite> favorites;
    long uid;
    int page = 1;
    /**
     * 用于获取微博信息流等操作的API
     */
    private FavoritesAPI favoritesAPI;


    /*
    *接口调取成功的处理方法
     */
    @Override
    public void complete(String response) {
        super.complete(response);
        if (!TextUtils.isEmpty(response)) {
            Log.e("RequestListener status", response);
            if (response.startsWith("{\"favorites\"")) {
                // 调用 StatusList#parse 解析字符串成微博列表对象
                try {
                    JSONObject object = new JSONObject(response);

                    List<Favorite> item = JSON.parseArray(object.getString("favorites"), Favorite.class);
                    if (item != null && item.size() > 0) {
                        if (loading.getVisibility() == View.VISIBLE) {
                            loading.setVisibility(View.GONE);
                            ptrFrameLayout.setVisibility(View.VISIBLE);
                        }
                        ptrFrameLayout.refreshComplete();
                        if (MODE == 1) {
                            favorites.clear();
                        }
                        favorites.addAll(item);
                        setState(0);
                        adapter.notifyDataSetChanged();
                        fresh = false;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(context, response, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void update() {
        super.update();
        MODE = 1;
        page = 1;
        favoritesAPI.favorites(20, page, mListener);
    }

    @Override
    public void init(View view) {
        super.init(view);
        uid = Long.valueOf(Main.user.id);
        favorites = new ArrayList<>();
        adapter = new FavoriteAdapter(context, favorites, 1);
        listView.setAdapter(adapter);


        // 对statusAPI实例化
        favoritesAPI = new FavoritesAPI(context, Constants.APP_KEY, mAccessToken);
        favoritesAPI.favorites(10, page, mListener);
    }

    @Override
    public void loadMore(int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        super.loadMore(firstVisibleItem, visibleItemCount, totalItemCount);
        int count=totalItemCount-firstVisibleItem;
        if (count>0&&count<5&&fresh==false){
            footview.setVisibility(View.VISIBLE);
            MODE = 2;
            fresh = true;
            setState(1);
            page++;
            favoritesAPI.favorites(20, page, mListener);
        }
    }
}

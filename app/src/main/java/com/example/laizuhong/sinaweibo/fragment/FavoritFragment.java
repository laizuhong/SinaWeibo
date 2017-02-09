package com.example.laizuhong.sinaweibo.fragment;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.laizuhong.sinaweibo.Constants;
import com.example.laizuhong.sinaweibo.MainActivity;
import com.example.laizuhong.sinaweibo.adapter.FavoriteRecyAdapter;
import com.sina.weibo.sdk.openapi.legacy.FavoritesAPI;
import com.sina.weibo.sdk.openapi.models.Favorite;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by laizuhong on 2015/11/12.
 */
public class FavoritFragment extends BaseRecyFragment{

    int MODE;
    boolean fresh = false;
    FavoriteRecyAdapter adapter;
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
//                    if (item != null && item.size() > 0) {
//                        if (loading.getVisibility() == View.VISIBLE) {
//                            loading.setVisibility(View.GONE);
//                            ptrFrameLayout.setVisibility(View.VISIBLE);
//                        }
//                        ptrFrameLayout.refreshComplete();
                    swipeRefreshLayout.setRefreshing(false);
                    if (item==null||item.size()==0){
                        adapter.notifyDataChangedAfterLoadMore(false);
                        return;
                    }
                        if (MODE == 1) {
                            favorites.clear();
                        }
                        favorites.addAll(item);
//                        setState(0);
                        adapter.notifyDataChangedAfterLoadMore(true);
                        fresh = false;
//                    }
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
    public void init() {
        super.init();
        uid = Long.valueOf(MainActivity.user.id);
        favorites = new ArrayList<>();
//        adapter = new FavoriteAdapter(context, favorites, 1);
//        listView.setAdapter(adapter);


        // 对statusAPI实例化
        favoritesAPI = new FavoritesAPI(context, Constants.APP_KEY, mAccessToken);
        favoritesAPI.favorites(20, page, mListener);


        adapter=new FavoriteRecyAdapter(context, favorites);

        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(adapter);
        adapter.openLoadMore(20, true);
        adapter.setLoadingView(footview);

        adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                MODE=2;
                page++;
                favoritesAPI.favorites(20, page, mListener);
            }
        });
        swipeRefreshLayout.post(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                swipeRefreshLayout.setRefreshing(true);
                update();
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                MODE=1;
                swipeRefreshLayout.setRefreshing(true);
                update();
            }
        });
    }

//    @Override
//    public void loadMore(int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//        super.loadMore(firstVisibleItem, visibleItemCount, totalItemCount);
//        int count=totalItemCount-firstVisibleItem;
//        if (count>0&&count<5&&fresh==false){
//            footview.setVisibility(View.VISIBLE);
//            MODE = 2;
//            fresh = true;
//            setState(1);
//            page++;
//            favoritesAPI.favorites(20, page, mListener);
//        }
//    }
}

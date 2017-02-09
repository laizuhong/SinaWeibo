package com.example.laizuhong.sinaweibo.fragment;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.laizuhong.sinaweibo.adapter.WeiboRecyAdapter;
import com.example.laizuhong.sinaweibo.config.Constants;
import com.example.laizuhong.sinaweibo.util.android.FriendsAPI;
import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.openapi.models.StatusList;

import java.util.ArrayList;

/**
 * Created by laizuhong on 2015/11/6.
 */
public class UserWeiboFragment extends BaseRecyFragment{

    int MODE;
    boolean fresh = false;
    WeiboRecyAdapter adapter;
    ArrayList<Status> statusList;
    long uid;
    int page = 1;
    /**
     * 用于获取微博信息流等操作的API
     */
    private FriendsAPI friendsAPI;


    @Override
    public void complete(String response) {
        super.complete(response);
        if (!TextUtils.isEmpty(response)) {
            Log.e("RequestListener status", response);
            if (response.startsWith("{\"statuses\"")) {
                // 调用 StatusList#parse 解析字符串成微博列表对象
                StatusList statuses = StatusList.parse(response);
                swipeRefreshLayout.setRefreshing(false);
                if (MODE == 1) {
                    statusList.clear();
                }
                statusList.addAll(statuses.statusList);
                adapter.notifyDataChangedAfterLoadMore(true);
                fresh = false;
            } else {
                Toast.makeText(context, response, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void init() {
        super.init();
//        sp = PreferenceManager.getDefaultSharedPreferences(context);
        friendsAPI = new FriendsAPI(context, Constants.APP_KEY, mAccessToken);
        friendsAPI.getUserWeibo(uid,null, 10, page, mListener);

        statusList = new ArrayList<>();



        adapter=new WeiboRecyAdapter(context, statusList);

        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(adapter);
        adapter.openLoadMore(20, true);
        adapter.setLoadingView(footview);

        adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                MODE=2;
                page++;
                friendsAPI.getUserWeibo(uid,null, 10, page, mListener);
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

    @Override
    public void update() {
        super.update();
        page = 1;
        friendsAPI.getUserWeibo(uid,null, 10, page, mListener);
    }
//
//    @Override
//    public void complete(String response) {
//        super.complete(response);
//        if (!TextUtils.isEmpty(response)) {
//            Log.e("RequestListener status", response);
//            if (response.startsWith("{\"statuses\"")) {
//                // 调用 StatusList#parse 解析字符串成微博列表对象
//                StatusList statuses = StatusList.parse(response);
//                if (statuses != null && statuses.total_number > 0) {
//                    if (loading.getVisibility() == View.VISIBLE) {
//                        loading.setVisibility(View.GONE);
//                        ptrFrameLayout.setVisibility(View.VISIBLE);
//                    }
//                    ptrFrameLayout.refreshComplete();
//                    if (MODE == 1) {
//                        statusList.clear();
//                    }
//                    statusList.addAll(statuses.statusList);
//                    setState(0);
//                    adapter.notifyDataSetChanged();
//                    fresh = false;
//                }
//            } else {
//                Toast.makeText(context, response, Toast.LENGTH_LONG).show();
//            }
//        }
//    }
//
//    @Override
//    public void update() {
//        super.update();
//        MODE = 1;
//        page = 1;
//        friendsAPI.getUserWeibo(uid,null, 10, page, mListener);
//    }
//
//    @Override
//    public void init(View view) {
//        super.init(view);
//        uid = Long.valueOf(MainActivity.user.id);
//        statusList = new ArrayList<>();
//        adapter = new WeiboAdapter(context, statusList, 1);
//        listView.setAdapter(adapter);
//        footview.setVisibility(View.GONE);
//
//
//        // 对statusAPI实例化
//        friendsAPI = new FriendsAPI(context, Constants.APP_KEY, mAccessToken);
//        friendsAPI.getUserWeibo(uid,null, 10, page, mListener);
//
//    }
//
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
//            friendsAPI.getUserWeibo(uid,null, 10, page, mListener);
//        }
//    }
}

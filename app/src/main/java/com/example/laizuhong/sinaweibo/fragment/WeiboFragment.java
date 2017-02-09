package com.example.laizuhong.sinaweibo.fragment;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.laizuhong.sinaweibo.adapter.WeiboRecyAdapter;
import com.example.laizuhong.sinaweibo.config.Constants;
import com.sina.weibo.sdk.openapi.StatusesAPI;
import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.openapi.models.StatusList;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by laizuhong on 2015/9/16.
 */
public class WeiboFragment extends BaseRecyFragment{

    int MODE=1;  //1为刷新，2为加载更多
    int page=1;
    boolean fresh=false;
    List<Status> statusList;
    WeiboRecyAdapter adapter;
//    SharedPreferences sp;
    /**
     * 用于获取微博信息流等操作的API
     */
    private StatusesAPI mStatusesAPI;


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
        // 对statusAPI实例化
        mStatusesAPI = new StatusesAPI(context, Constants.APP_KEY, mAccessToken);

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
                mStatusesAPI.friendsTimeline(0L, 0L, 20, page, false, 0, false, mListener);
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
        mStatusesAPI.friendsTimeline(0L, 0L, 20, page, false, 0, false, mListener);
    }

}

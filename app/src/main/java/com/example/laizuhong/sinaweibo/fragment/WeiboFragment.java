package com.example.laizuhong.sinaweibo.fragment;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.laizuhong.sinaweibo.adapter.WeiboAdapter;
import com.example.laizuhong.sinaweibo.config.Constants;
import com.example.laizuhong.sinaweibo.util.MyLog;
import com.sina.weibo.sdk.openapi.StatusesAPI;
import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.openapi.models.StatusList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by laizuhong on 2015/9/16.
 */
public class WeiboFragment extends BaseFragment{

    int MODE=1;  //1为刷新，2为加载更多
    int page=1;
    boolean fresh=false;
    List<Status> statusList;
    WeiboAdapter adapter;
    SharedPreferences sp;
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
                if (statuses != null && statuses.statusList != null && statuses.statusList.size() > 0) {
                    if (loading.getVisibility() == View.VISIBLE) {
                        loading.setVisibility(View.GONE);
                        ptrFrameLayout.setVisibility(View.VISIBLE);
                    }
                    ptrFrameLayout.refreshComplete();
                    if (MODE == 1) {
                        statusList.clear();
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("weibo", response);
                        editor.commit();
                    }
                    statusList.addAll(statuses.statusList);
                    setState(0);
                    adapter.notifyDataSetChanged();
                    fresh = false;
                } else {
                    setState(3);
                }
            } else {
                Toast.makeText(context, response, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void init(View v) {
        super.init(v);
        sp = PreferenceManager.getDefaultSharedPreferences(context);
        // 对statusAPI实例化
        mStatusesAPI = new StatusesAPI(context, Constants.APP_KEY, mAccessToken);

        String weibo = sp.getString("weibo", null);
        if (weibo == null) {
            statusList = new ArrayList<>();
        } else {
            StatusList list = StatusList.parse(weibo);
            if (list != null && list.statusList != null && list.statusList.size() > 0) {
                statusList = list.statusList;
                loading.setVisibility(View.GONE);
                ptrFrameLayout.setVisibility(View.VISIBLE);
            }
        }
        adapter = new WeiboAdapter(context, statusList, 1);
        listView.setAdapter(adapter);
        footview.setVisibility(View.GONE);
        MyLog.e("weboadapter init");
        if (statusList.size() == 0) {
            mStatusesAPI.friendsTimeline(0L, 0L, 20, page, false, 0, false, mListener);
        }
    }

    @Override
    public void update() {
        super.update();
        MODE = 1;
        page = 1;
        mStatusesAPI.friendsTimeline(0L, 0L, 20, page, false, 0, false, mListener);
    }

    @Override
    public void loadMore(int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        super.loadMore(firstVisibleItem, visibleItemCount, totalItemCount);
        int count=totalItemCount-firstVisibleItem;
        if (count>0&&count<5&&fresh==false){
            fresh = true;
            footview.setVisibility(View.VISIBLE);
            MODE=2;
            setState(1);
            page++;

            mStatusesAPI.friendsTimeline(0L, 0L, 20, page, false, 0, false, mListener);
        }
    }
}

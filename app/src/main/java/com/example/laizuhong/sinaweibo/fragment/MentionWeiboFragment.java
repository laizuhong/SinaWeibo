package com.example.laizuhong.sinaweibo.fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.laizuhong.sinaweibo.Constants;
import com.example.laizuhong.sinaweibo.UserActivity;
import com.example.laizuhong.sinaweibo.adapter.WeiboAdapter;
import com.example.laizuhong.sinaweibo.util.android.FriendsAPI;
import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.openapi.models.StatusList;

import java.util.ArrayList;

/**
 * Created by laizuhong on 2015/11/10.
 */
public class MentionWeiboFragment extends BaseFragment {


    int MODE;
    boolean fresh = false;
    WeiboAdapter adapter;
    ArrayList<Status> statusList;
    int page = 1;
    String username;
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
                if (statuses != null && statuses.total_number > 0) {
                    if (loading.getVisibility() == View.VISIBLE) {
                        loading.setVisibility(View.GONE);
                        ptrFrameLayout.setVisibility(View.VISIBLE);
                    }
                    ptrFrameLayout.refreshComplete();
                    if (MODE == 1) {
                        statusList.clear();
                    }
                    statusList.addAll(statuses.statusList);
                    setState(0);
                    adapter.notifyDataSetChanged();
                    fresh = false;
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
        friendsAPI.getUserWeibo(0,username, 10, page, mListener);
    }

    @Override
    public void init(View view) {
        super.init(view);
        username=((UserActivity) getActivity()).getName();
        statusList = new ArrayList<>();
        adapter = new WeiboAdapter(context, statusList, 1);
        listView.setAdapter(adapter);
        footview.setVisibility(View.GONE);


        // 对statusAPI实例化
        friendsAPI = new FriendsAPI(context, Constants.APP_KEY, mAccessToken);
        friendsAPI.getUserWeibo(0,username, 10, page, mListener);

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
            friendsAPI.getUserWeibo(0,username, 10, page, mListener);
        }
    }
}

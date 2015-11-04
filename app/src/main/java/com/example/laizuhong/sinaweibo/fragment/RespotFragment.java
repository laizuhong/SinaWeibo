package com.example.laizuhong.sinaweibo.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.example.laizuhong.sinaweibo.Constants;
import com.example.laizuhong.sinaweibo.R;
import com.example.laizuhong.sinaweibo.WeiboDetailActivity;
import com.example.laizuhong.sinaweibo.adapter.RepostAdapter;
import com.example.laizuhong.sinaweibo.bean.Repost;
import com.example.laizuhong.sinaweibo.config.AccessTokenKeeper;
import com.example.laizuhong.sinaweibo.util.MyLog;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.StatusesAPI;
import com.sina.weibo.sdk.openapi.models.ErrorInfo;
import com.sina.weibo.sdk.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by laizuhong on 2015/9/30.
 */
public class RespotFragment extends Fragment implements AbsListView.OnScrollListener {

    ListView listView;
    Context context;
    boolean fresh = false;
    int page = 1;
    List<Repost> reposts;
    ScrollView scrollView;
    RepostAdapter adapter;
    boolean isfirst = true;
    /**
     * 当前 Token 信息
     */
    private Oauth2AccessToken mAccessToken;
    /**
     * 用于获取微博信息流等操作的API
     */
    private StatusesAPI mStatusesAPI;
    private long id;
    private View mProgressBar;
    private TextView mHintView;
    private View footview, rootView;
    /**
     * 微博 OpenAPI 回调接口。
     */
    private RequestListener mListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                Log.e("RequestListener status", response);
                if (response.startsWith("{\"reposts\"")) {
                    try {
                        JSONObject object = new JSONObject(response);
                        List<Repost> item = JSON.parseArray(object.getString("reposts"), Repost.class);
                        if (item == null || item.size() == 0) {
                            fresh = true;
                            setState(0);
                        } else {
                            reposts.addAll(item);
                            adapter.notifyDataSetChanged();
                            setState(0);
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
        public void onWeiboException(WeiboException e) {
            LogUtil.e("onWeiboException", e.getMessage());
            ErrorInfo info = ErrorInfo.parse(e.getMessage());
            Toast.makeText(context, info.toString(), Toast.LENGTH_LONG).show();
        }
    };



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_repost, null);
        init(view);
        return view;
    }

    private void init(View view) {
        id = Long.valueOf(((WeiboDetailActivity) getActivity()).getWeibo_id());
        context = getActivity();
        listView = (ListView) view.findViewById(R.id.listView);
        footview = LayoutInflater.from(context)
                .inflate(R.layout.xlistview_footer, null);
        mProgressBar = footview.findViewById(R.id.xlistview_footer_progressbar);
        mHintView = (TextView) footview
                .findViewById(R.id.xlistview_footer_hint_textview);

        reposts = new ArrayList<>();
        adapter = new RepostAdapter(context, reposts);
        listView.addFooterView(footview);
        listView.setAdapter(adapter);
        listView.setOnScrollListener(this);


        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    MyLog.e("up   " + WeiboDetailActivity.butom + "    " + isfirst);
                    if (!WeiboDetailActivity.butom) {
                        if (isfirst) {
                            scrollView.requestDisallowInterceptTouchEvent(false);
                        }
                    }
                } else {
                    scrollView.requestDisallowInterceptTouchEvent(true);
                }
                return false;
            }
        });

        // 获取当前已保存过的 Token
        mAccessToken = AccessTokenKeeper.readAccessToken(context);

        mStatusesAPI = new StatusesAPI(context, Constants.APP_KEY, mAccessToken);

        mStatusesAPI.getWeiboRepost(id, 10, page, mListener);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        Log.e("comment  onScroll", "first=" + firstVisibleItem + "    visible=" + visibleItemCount + "     total=" + totalItemCount);
        isfirst = firstVisibleItem == 0;
        if (totalItemCount - firstVisibleItem == visibleItemCount && fresh == false) {
            footview.setVisibility(View.VISIBLE);
            fresh = true;
            setState(1);
            page++;
            mStatusesAPI.getWeiboRepost(id, 10, page, mListener);
        }
    }


    private void setState(int state) {
        mProgressBar.setVisibility(View.GONE);

        if (state == 0) {
            mHintView.setText("加载更多");
        } else if (state == 1) {// 当加载的时候
            mProgressBar.setVisibility(View.VISIBLE);// 加载进度条显示
            mHintView.setText("正在加载");
        }
    }

}

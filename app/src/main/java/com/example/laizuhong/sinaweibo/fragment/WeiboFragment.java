package com.example.laizuhong.sinaweibo.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.laizuhong.sinaweibo.R;
import com.example.laizuhong.sinaweibo.SendWeiboActivity;
import com.example.laizuhong.sinaweibo.adapter.WeiboAdapter;
import com.example.laizuhong.sinaweibo.config.Constants;
import com.example.laizuhong.sinaweibo.util.DisplayUtil;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.StatusesAPI;
import com.sina.weibo.sdk.openapi.models.ErrorInfo;
import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.openapi.models.StatusList;
import com.sina.weibo.sdk.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;

/**
 * Created by laizuhong on 2015/9/16.
 */
public class WeiboFragment extends Fragment implements AbsListView.OnScrollListener{

    Context context;
    @Bind(R.id.store_house_ptr_frame)
    PtrFrameLayout ptrFrameLayout;
    @Bind(R.id.allweibo)
    ListView listView;
    @Bind(R.id.loading)
    LinearLayout loading;
    int MODE=1;  //1为刷新，2为加载更多
    int page=1;
    boolean fresh=false;
    List<Status> statusList;
    WeiboAdapter adapter;
    SharedPreferences sp;
    private View rootView;//缓存Fragment view
    /**
     * 当前 Token 信息
     */
    private Oauth2AccessToken mAccessToken;
    /**
     * 用于获取微博信息流等操作的API
     */
    private StatusesAPI mStatusesAPI;
    private View mProgressBar;
    private TextView mHintView;
    private View footview;
    /**
     * 微博 OpenAPI 回调接口。
     */
    private RequestListener mListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
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
        public void onWeiboException(WeiboException e) {
            LogUtil.e("onWeiboException", e.getMessage());
            ErrorInfo info = ErrorInfo.parse(e.getMessage());
            Toast.makeText(context, info.toString(), Toast.LENGTH_LONG).show();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(rootView==null){
            rootView=inflater.inflate(R.layout.fragment_weibo, null);
        }
        //缓存的rootView需要判断是否已经被加过parent， 如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }
        context=getActivity();
        ButterKnife.bind(this, rootView);
        init(rootView);
        return rootView;
    }

    private void init(View v){
        sp = PreferenceManager.getDefaultSharedPreferences(context);
//        listView= (ListView) v.findViewById(R.id.allweibo);

        // 获取当前已保存过的 Token
        mAccessToken = com.example.laizuhong.sinaweibo.config.AccessTokenKeeper.readAccessToken(context);
        // 对statusAPI实例化
        mStatusesAPI = new StatusesAPI(context, Constants.APP_KEY, mAccessToken);

        footview = LayoutInflater.from(context)
                .inflate(R.layout.xlistview_footer, null);
        mProgressBar = footview.findViewById(R.id.xlistview_footer_progressbar);
        mHintView = (TextView) footview
                .findViewById(R.id.xlistview_footer_hint_textview);

//        loading= (LinearLayout) v.findViewById(R.id.loading);
        loading.setVisibility(View.VISIBLE);


//        ptrFrameLayout= (PtrFrameLayout) v.findViewById(R.id.store_house_ptr_frame);
        ptrFrameLayout.setVisibility(View.GONE);
        // header
        final MaterialHeader header = new MaterialHeader(context);
        int[] colors = getResources().getIntArray(R.array.google_colors);
        header.setColorSchemeColors(colors);
        header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
        header.setPadding(0, 0, 0, DisplayUtil.dip2px(context, 10));
        header.setPtrFrameLayout(ptrFrameLayout);

        ptrFrameLayout.setResistance(1.7f);
        ptrFrameLayout.setRatioOfHeaderHeightToRefresh(1.2f);
        ptrFrameLayout.setDurationToClose(200);

        ptrFrameLayout.setLoadingMinTime(1000);
        ptrFrameLayout.setDurationToCloseHeader(500);
        ptrFrameLayout.setHeaderView(header);
        ptrFrameLayout.addPtrUIHandler(header);
        ptrFrameLayout.setPtrHandler(new PtrHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                frame.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        MODE = 1;
                        page = 1;
                        mStatusesAPI.friendsTimeline(0L, 0L, 20, page, false, 0, false, mListener);

                    }
                }, 0);
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }
        });

        v.findViewById(R.id.tosend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SendWeiboActivity.class);
                context.startActivity(intent);
            }
        });


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
        listView.addFooterView(footview);
        listView.setAdapter(adapter);
        footview.setVisibility(View.GONE);
        listView.setOnScrollListener(this);

        if (statusList.size() == 0) {
            mStatusesAPI.friendsTimeline(0L, 0L, 20, page, false, 0, false, mListener);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        //Log.e("onscroll",totalItemCount-firstVisibleItem+"   "+visibleItemCount);
        if (totalItemCount-firstVisibleItem==visibleItemCount&&fresh==false){
            fresh = true;
            footview.setVisibility(View.VISIBLE);
            MODE=2;
            setState(1);
            page++;

            mStatusesAPI.friendsTimeline(0L, 0L, 20, page, false, 0, false, mListener);
        }
    }


    private void setState(int state){
        mProgressBar.setVisibility(View.GONE);

        if (state == 0) {
            mHintView.setText("加载更多");
        } else if (state == 1) {// 当加载的时候
            mProgressBar.setVisibility(View.VISIBLE);// 加载进度条显示
            mHintView.setText("正在加载");
        } else if (state == 3) {
            mHintView.setText("没有更多");
        }
    }

}

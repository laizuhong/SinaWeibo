package com.example.laizuhong.sinaweibo;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.laizuhong.sinaweibo.adapter.WeiboAdapter;
import com.example.laizuhong.sinaweibo.config.AccessTokenKeeper;
import com.example.laizuhong.sinaweibo.util.DisplayUtil;
import com.example.laizuhong.sinaweibo.util.android.FriendsAPI;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.models.ErrorInfo;
import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.openapi.models.StatusList;
import com.sina.weibo.sdk.utils.LogUtil;

import java.util.ArrayList;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;

/**
 * Created by laizuhong on 2015/9/16.
 */
public class UserWeiboActivity extends BaseActivity implements AbsListView.OnScrollListener {

    ListView listView;
    LinearLayout loading;
    int MODE;
    boolean fresh = false;
    WeiboAdapter adapter;
    ArrayList<Status> statusList;
    PtrFrameLayout ptrFrameLayout;
    long uid;
    int page = 1;
    /** 当前 Token 信息 */
    private Oauth2AccessToken mAccessToken;
    /** 用于获取微博信息流等操作的API */
    private FriendsAPI friendsAPI;
    private View footview;
    private View mProgressBar;
    private TextView mHintView;


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
                    Toast.makeText(UserWeiboActivity.this, response, Toast.LENGTH_LONG).show();
                }
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            LogUtil.e("onWeiboException", e.getMessage());
            ErrorInfo info = ErrorInfo.parse(e.getMessage());
            Toast.makeText(UserWeiboActivity.this, info.toString(), Toast.LENGTH_LONG).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_all_weibo);
        uid = Long.valueOf(getIntent().getStringExtra("uid"));
        String name = getIntent().getStringExtra("name");
        getSupportActionBar().setTitle(name);
        init();
    }

    private void init(){
        listView = (ListView) findViewById(R.id.user_weibo_list);
        footview = LayoutInflater.from(this)
                .inflate(R.layout.xlistview_footer, null);
        mProgressBar = footview.findViewById(R.id.xlistview_footer_progressbar);
        mHintView = (TextView) footview
                .findViewById(R.id.xlistview_footer_hint_textview);
        statusList = new ArrayList<>();
        adapter = new WeiboAdapter(this, statusList, 1);
        listView.addFooterView(footview);
        listView.setAdapter(adapter);
        footview.setVisibility(View.GONE);
        listView.setOnScrollListener(this);




        // 获取当前已保存过的 Token
        mAccessToken = AccessTokenKeeper.readAccessToken(this);
        // 对statusAPI实例化
        friendsAPI = new FriendsAPI(this, Constants.APP_KEY, mAccessToken);
        friendsAPI.getUserWeibo(uid,null, 10, page, mListener);


        ptrFrameLayout = (PtrFrameLayout) findViewById(R.id.store_house_ptr_frame);
        ptrFrameLayout.setVisibility(View.GONE);
        // header
        final MaterialHeader header = new MaterialHeader(this);
        int[] colors = getResources().getIntArray(R.array.google_colors);
        header.setColorSchemeColors(colors);
        header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
        header.setPadding(0, 0, 0, DisplayUtil.dip2px(this, 10));
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
                        friendsAPI.getUserWeibo(uid,null, 10, page, mListener);

                    }
                }, 0);
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }
        });


        loading = (LinearLayout) findViewById(R.id.loading);
        loading.setVisibility(View.VISIBLE);

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        //Log.e("onscroll",totalItemCount-firstVisibleItem+"   "+visibleItemCount);
        int count=totalItemCount-firstVisibleItem;
        if (count>0&&count<5&&fresh==false){
            footview.setVisibility(View.VISIBLE);
            MODE = 2;
            fresh = true;
            setState(1);
            page++;
            friendsAPI.getUserWeibo(uid,null, 10, page, mListener);
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

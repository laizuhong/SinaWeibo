package com.example.laizuhong.sinaweibo.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import com.alibaba.fastjson.JSON;
import com.example.laizuhong.sinaweibo.Constants;
import com.example.laizuhong.sinaweibo.Main;
import com.example.laizuhong.sinaweibo.R;
import com.example.laizuhong.sinaweibo.adapter.FavoriteAdapter;
import com.example.laizuhong.sinaweibo.config.AccessTokenKeeper;
import com.example.laizuhong.sinaweibo.util.DisplayUtil;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.legacy.FavoritesAPI;
import com.sina.weibo.sdk.openapi.models.ErrorInfo;
import com.sina.weibo.sdk.openapi.models.Favorite;
import com.sina.weibo.sdk.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;

/**
 * Created by laizuhong on 2015/11/12.
 */
public class FavoritFragment extends Fragment implements AbsListView.OnScrollListener {

    Context context;
    ListView listView;
    LinearLayout loading;
    int MODE;
    boolean fresh = false;
    FavoriteAdapter adapter;
    ArrayList<Favorite> favorites;
    PtrFrameLayout ptrFrameLayout;
    long uid;
    int page = 1;
    /**
     * 当前 Token 信息
     */
    private Oauth2AccessToken mAccessToken;
    /**
     * 用于获取微博信息流等操作的API
     */
    private FavoritesAPI favoritesAPI;
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
        public void onWeiboException(WeiboException e) {
            LogUtil.e("onWeiboException", e.getMessage());
            ErrorInfo info = ErrorInfo.parse(e.getMessage());
            Toast.makeText(context, info.toString(), Toast.LENGTH_LONG).show();
        }
    };


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite, null);
        context = getActivity();
        init(view);
        return view;

    }


    private void init(View view) {
        uid = Long.valueOf(Main.user.id);
        listView = (ListView) view.findViewById(R.id.user_weibo_list);
        footview = LayoutInflater.from(context)
                .inflate(R.layout.xlistview_footer, null);
        mProgressBar = footview.findViewById(R.id.xlistview_footer_progressbar);
        mHintView = (TextView) footview
                .findViewById(R.id.xlistview_footer_hint_textview);
        favorites = new ArrayList<>();
        adapter = new FavoriteAdapter(context, favorites, 1);
        listView.addFooterView(footview);
        listView.setAdapter(adapter);
        footview.setVisibility(View.GONE);
        listView.setOnScrollListener(this);


        // 获取当前已保存过的 Token
        mAccessToken = AccessTokenKeeper.readAccessToken(context);
        // 对statusAPI实例化
        favoritesAPI = new FavoritesAPI(context, Constants.APP_KEY, mAccessToken);
        favoritesAPI.favorites(20, page, mListener);


        ptrFrameLayout = (PtrFrameLayout) view.findViewById(R.id.store_house_ptr_frame);
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
                        favoritesAPI.favorites(20, page, mListener);

                    }
                }, 0);
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }
        });


        loading = (LinearLayout) view.findViewById(R.id.loading);
        loading.setVisibility(View.VISIBLE);

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        //Log.e("onscroll",totalItemCount-firstVisibleItem+"   "+visibleItemCount);
        if (totalItemCount - firstVisibleItem == visibleItemCount && fresh == false) {
            footview.setVisibility(View.VISIBLE);
            MODE = 2;
            fresh = true;
            setState(1);
            page++;
            favoritesAPI.favorites(20, page, mListener);
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

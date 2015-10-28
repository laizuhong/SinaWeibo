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
import android.widget.ListView;
import android.widget.TextView;

import com.example.laizuhong.sinaweibo.Constants;
import com.example.laizuhong.sinaweibo.R;
import com.example.laizuhong.sinaweibo.WeiboDetailActivity;
import com.example.laizuhong.sinaweibo.adapter.CommentAdapter;
import com.example.laizuhong.sinaweibo.config.AccessTokenKeeper;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.CommentsAPI;
import com.sina.weibo.sdk.openapi.models.Comment;
import com.sina.weibo.sdk.openapi.models.CommentList;
import com.sina.weibo.sdk.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by laizuhong on 2015/9/30.
 */
public class CommentFragment extends Fragment implements AbsListView.OnScrollListener {

    CommentAdapter adapter;
    List<Comment> commentlist;
    ListView listView;
    Context context;
    CommentsAPI commentsAPI;
    Oauth2AccessToken oauth2AccessToken;
    boolean fresh = false;
    int page = 1;
    private long id;
    private View mProgressBar;
    private TextView mHintView;
    private View footview;
    /**
     * 微博 OpenAPI 回调接口。
     */
    private RequestListener mListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            Log.e("onComplete", response);
            if (!TextUtils.isEmpty(response)) {
                CommentList comments = CommentList.parse(response);
                if (comments != null && comments.total_number > 0 && comments.commentList != null && comments.commentList.size() > 0) {

                    commentlist.addAll(comments.commentList);
                    adapter.notifyDataSetChanged();
                    fresh = false;
                    setState(0);
                }
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            LogUtil.e("onWeiboException", e.getMessage());
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

        commentlist = new ArrayList<>();
        adapter = new CommentAdapter(commentlist, context);
        listView.addFooterView(footview);
        listView.setAdapter(adapter);
        //footview.setVisibility(View.GONE);
        listView.setOnScrollListener(this);

        // 获取当前已保存过的 Token
        oauth2AccessToken = AccessTokenKeeper.readAccessToken(context);
        // 获取微博评论信息接口
        commentsAPI = new CommentsAPI(context, Constants.APP_KEY, oauth2AccessToken);

        commentsAPI.show(id, 0, 0, 10, page, CommentsAPI.AUTHOR_FILTER_ALL, mListener);

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        Log.e("onScroll", "first=" + firstVisibleItem + "    visible=" + visibleItemCount + "     total=" + totalItemCount);
        if (totalItemCount - firstVisibleItem == visibleItemCount && fresh == false) {
            footview.setVisibility(View.VISIBLE);
            fresh = true;
            setState(1);
            page++;
            commentsAPI.show(id, 0, 0, 10, page, CommentsAPI.AUTHOR_FILTER_ALL, mListener);
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

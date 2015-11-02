package com.example.laizuhong.sinaweibo.util;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * Created by LAIZUHONG on 2015/10/2.
 */
public class MyScrollView extends ScrollView {

    private OnScrollListener onScrollListener;
    private OnScrollBottomListener onScrollBottomListener;

    public MyScrollView(Context context) {
        this(context, null);
    }

    public MyScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public OnScrollBottomListener getOnScrollBottomListener() {
        return onScrollBottomListener;
    }

    public void setOnScrollBottomListener(OnScrollBottomListener onScrollBottomListener) {
        this.onScrollBottomListener = onScrollBottomListener;
    }

    /**
     * 设置滚动接口
     *
     * @param onScrollListener
     */
    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {// 滑动改变就会实时调用
        super.onScrollChanged(l, t, oldl, oldt);
//        if (onScrollListener != null) {
//            onScrollListener.onScroll(t);
//        }
//        if (t+getHeight()==computeVerticalScrollRange()){
//            onScrollBottomListener.scrollBottom();
//        }
    }

    /**
     * 滚动的回调接口
     */
    public interface OnScrollListener {
        /**
         * 回调方法， 返回MyScrollView滑动的Y方向距离
         *
         * @param scrollY 、
         */
        void onScroll(int scrollY);
    }


    public interface OnScrollBottomListener {
        void scrollBottom();
    }

}

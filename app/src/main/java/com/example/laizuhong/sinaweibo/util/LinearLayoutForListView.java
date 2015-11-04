package com.example.laizuhong.sinaweibo.util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by laizuhong on 2015/11/3.
 */
public class LinearLayoutForListView extends LinearLayout {

    private AdapterForLinearLayout adapter;

    private OnClickListener onClickListener = null;

//注意这两个构造函数一定要继承，否则后果自负

    public LinearLayoutForListView(Context context) {

        super(context);

    }


    public LinearLayoutForListView(Context context, AttributeSet attrs) {

        super(context, attrs);

    }

//我们把所有的子项加到当前这个linearlayout中去，注意一下，这里面要为每个子项添加上触发事件，我这里只是简单的实现功能，异常处理什么的统统没加

    public void bindLinearLayout() {

        for (int i = 0; i < adapter.getCount(); i++) {

            View v = adapter.getView(i, null, null);


            v.setOnClickListener(this.onClickListener);

            this.addView(v, i);

        }

    }

//下面的这四个就不用解释了，get,set方法

    public AdapterForLinearLayout getAdapter() {

        return adapter;

    }


    public void setAdapter(AdapterForLinearLayout adapter) {

        this.adapter = adapter;

        bindLinearLayout();

    }


    public OnClickListener getOnClickListener() {

        return onClickListener;

    }


    public void setOnClickListener(OnClickListener onClickListener) {

        this.onClickListener = onClickListener;

    }
}

package com.example.laizuhong.sinaweibo.util;

import android.widget.GridView;

/**
 * Created by laizuhong on 2015/9/17.
 */
public class MyGridView extends GridView
{
    public MyGridView(android.content.Context context,
                      android.util.AttributeSet attrs)
    {
        super(context, attrs);
    }

    /**
     * 设置不滚动
     */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);

    }

}

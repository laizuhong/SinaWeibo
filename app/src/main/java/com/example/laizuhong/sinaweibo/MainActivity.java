package com.example.laizuhong.sinaweibo;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.example.laizuhong.sinaweibo.fragment.MessageFragment;
import com.example.laizuhong.sinaweibo.fragment.MineFragment;
import com.example.laizuhong.sinaweibo.fragment.SearchFragment;
import com.example.laizuhong.sinaweibo.fragment.WeiboFragment;
import com.example.laizuhong.sinaweibo.util.FragmentTabHost;


public class MainActivity extends AppCompatActivity {

    public static MainActivity mainActivity;
    ActionBar actionBar;
    //定义FragmentTabHost对象
    private FragmentTabHost mTabHost;
    //定义一个布局
    private LayoutInflater layoutInflater;
    //定义数组来存放Fragment界面
    private Class fragmentArray[] = {WeiboFragment.class, MessageFragment.class, SearchFragment.class, MineFragment.class};
    //定义数组来存放按钮图片
    private int mImageViewArray[] = {R.drawable.tab_weibo_selector,R.drawable.tab_message_selector,
    R.drawable.tab_search_selector,R.drawable.tab_mine_selector};
    //Tab选项卡的文字
    private String mTextviewArray[] = {"首页", "消息", "广场", "我"};

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainActivity=this;
        MyApp.width=getWindowManager().getDefaultDisplay().getWidth();
        initView();
    }

    /**
     * 初始化组件
     */
    private void initView(){
        //实例化布局对象
        layoutInflater = LayoutInflater.from(this);

        //实例化TabHost对象，得到TabHost
        mTabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
        //得到fragment的个数
        int count = fragmentArray.length;

        for(int i = 0; i < count; i++){
            //为每一个Tab按钮设置图标、文字和内容
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(mTextviewArray[i]).setIndicator(getTabItemView(i));
            //将Tab按钮添加进Tab选项卡中
            mTabHost.addTab(tabSpec, fragmentArray[i], null);
            //设置Tab按钮的背景
            //mTabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.selector_tab_background);
        }
    }

    /**
     * 给Tab按钮设置图标和文字
     */
    private View getTabItemView(int index){
        View view = layoutInflater.inflate(R.layout.tab_item_view, null);

        ImageView imageView = (ImageView) view.findViewById(R.id.imageview);
        imageView.setImageResource(mImageViewArray[index]);

        TextView textView = (TextView) view.findViewById(R.id.textview);
        textView.setText(mTextviewArray[index]);

        return view;
    }

}

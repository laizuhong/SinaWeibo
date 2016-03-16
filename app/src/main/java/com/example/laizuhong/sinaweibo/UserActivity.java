package com.example.laizuhong.sinaweibo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.laizuhong.sinaweibo.fragment.MentionWeiboFragment;
import com.example.laizuhong.sinaweibo.fragment.UserFragment;
import com.example.laizuhong.sinaweibo.util.MyLog;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by laizuhong on 2015/11/9.
 */
public class UserActivity extends AppCompatActivity implements View.OnClickListener {

    String name;
    UserFragment userFragment;
    MentionWeiboFragment mentionWeiboFragment;
    TextView tv_name, info, weibo;
    ImageView img;


    public String getName() {
        return name;
    }

    public void initImg(String path) {
        ImageLoader.getInstance().displayImage(path, img, MyApp.options);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyLog.e(getIntent().getData() + "");
        setContentView(R.layout.activity_user);
        Toolbar toolbar= (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setTitle("发微博");
        toolbar.setNavigationIcon(R.drawable.ic_menu_back);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        if (name == null) {
            name = getIntent().getData().toString().replace("erciyuan.mention://", "");
        }
        getSupportActionBar().setTitle(name);
        init();
    }

    private void init() {
        img = (ImageView) findViewById(R.id.imageView1);
        tv_name = (TextView) findViewById(R.id.name);
        tv_name.setText(name);
        info = (TextView) findViewById(R.id.info);
        info.setOnClickListener(this);
        weibo = (TextView) findViewById(R.id.weibo);
        weibo.setOnClickListener(this);
        switchFragment(0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.info:
                switchFragment(0);
                break;
            case R.id.weibo:
                switchFragment(1);
                break;
        }
    }


    private void switchFragment(int tab) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        hideFragment(transaction);
        switch (tab) {
            case 0:
                if (userFragment == null) {
                    userFragment = new UserFragment();
                    transaction.add(R.id.user_fra, userFragment);
                } else {
                    transaction.show(userFragment);
                }
                break;
            case 1:
                if (mentionWeiboFragment == null) {
                    mentionWeiboFragment = new MentionWeiboFragment();
                    transaction.add(R.id.user_fra, mentionWeiboFragment);
                } else {
                    transaction.show(mentionWeiboFragment);
                }
                break;

        }
        transaction.commit();
    }


    private void hideFragment(FragmentTransaction transaction) {
        if (userFragment != null) {
            transaction.hide(userFragment);
        }
        if (mentionWeiboFragment != null) {
            transaction.hide(mentionWeiboFragment);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:// 点击返回图标事件
                finish();
            default:
            return super.onOptionsItemSelected(item);
        }
    }
}

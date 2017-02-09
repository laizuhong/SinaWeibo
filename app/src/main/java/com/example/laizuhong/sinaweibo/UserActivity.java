package com.example.laizuhong.sinaweibo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.laizuhong.sinaweibo.fragment.MentionWeiboFragment;
import com.example.laizuhong.sinaweibo.fragment.UserFragment;
import com.example.laizuhong.sinaweibo.util.MyLog;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by laizuhong on 2015/11/9.
 */
public class UserActivity extends AppCompatActivity {

    String name;
    UserFragment userFragment;
    MentionWeiboFragment mentionWeiboFragment;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
//    @Bind(R.id.imageView1)
//    ImageView imageView1;
//    @Bind(R.id.name)
//    TextView tvName;
//    @Bind(R.id.sticky_header)
//    LinearLayout stickyHeader;
    @Bind(R.id.tabs)
    TabLayout tabs;
    @Bind(R.id.viewPager)
    ViewPager viewPager;
//    @Bind(R.id.nestedScrollView)
//    NestedScrollView nestedScrollView;
//    TextView tv_name, info, weibo;
//    ImageView img;

    List<String> list = new ArrayList<>();
    MyPagerAdapter adapter;

    public String getName() {
        return name;
    }

    public void initImg(String path) {
//        ImageLoader.getInstance().displayImage(path, imageView1, MyApp.options);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyLog.e(getIntent().getData() + "");
        setContentView(R.layout.activity_user);
        ButterKnife.bind(this);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setTitle("发微博");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        if (name == null) {
            name = getIntent().getData().toString().replace("erciyuan.mention://", "");
        }
        getSupportActionBar().setTitle(name);
        init();
    }

    private void init() {
//        img = (ImageView) findViewById(R.id.imageView1);
//        tv_name = (TextView) findViewById(R.id.name);
//        tvName.setText(name);
//        info = (TextView) findViewById(R.id.info);
//        info.setOnClickListener(this);
//        weibo = (TextView) findViewById(R.id.weibo);
//        weibo.setOnClickListener(this);
//        switchFragment(0);
//        nestedScrollView.setFillViewport(true);
        list.add("主页");
        list.add("微博");
        adapter = new MyPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tabs.setupWithViewPager(viewPager);
    }

//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.info:
//                switchFragment(0);
//                break;
//            case weibo:
//                switchFragment(1);
//                break;
//        }
//    }
//
//
//    private void switchFragment(int tab) {
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        hideFragment(transaction);
//        switch (tab) {
//            case 0:
//                if (userFragment == null) {
//                    userFragment = new UserFragment();
//                    transaction.add(R.id.user_fra, userFragment);
//                } else {
//                    transaction.show(userFragment);
//                }
//                break;
//            case 1:
//                if (mentionWeiboFragment == null) {
//                    mentionWeiboFragment = new MentionWeiboFragment();
//                    transaction.add(R.id.user_fra, mentionWeiboFragment);
//                } else {
//                    transaction.show(mentionWeiboFragment);
//                }
//                break;
//
//        }
//        transaction.commit();
//    }
//
//
//    private void hideFragment(FragmentTransaction transaction) {
//        if (userFragment != null) {
//            transaction.hide(userFragment);
//        }
//        if (mentionWeiboFragment != null) {
//            transaction.hide(mentionWeiboFragment);
//        }
//
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:// 点击返回图标事件
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public class MyPagerAdapter extends FragmentPagerAdapter {

        List<Fragment> fragments = new ArrayList<>();

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
            for (int i = 0; i < list.size(); i++) {
                if (i == 0) {
                    UserFragment fragment = new UserFragment();
//                    Bundle args = new Bundle();
//                    args.putInt("p",i);
//                    fragment.setArguments(args);
                    fragments.add(fragment);
                } else {
                    MentionWeiboFragment fragment = new MentionWeiboFragment();
//                    Bundle args = new Bundle();
//                    args.putInt("p",i);
//                    fragment.setArguments(args);
                    fragments.add(fragment);
                }
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return list.get(position);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }
    }
}

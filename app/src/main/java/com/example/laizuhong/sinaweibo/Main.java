package com.example.laizuhong.sinaweibo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.laizuhong.sinaweibo.config.AccessTokenKeeper;
import com.example.laizuhong.sinaweibo.fragment.FavoritFragment;
import com.example.laizuhong.sinaweibo.fragment.FriendsFragment;
import com.example.laizuhong.sinaweibo.fragment.SettingFragment;
import com.example.laizuhong.sinaweibo.fragment.UserWeiboFragment;
import com.example.laizuhong.sinaweibo.fragment.WeiboFragment;
import com.example.laizuhong.sinaweibo.util.MyLog;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.UsersAPI;
import com.sina.weibo.sdk.openapi.models.User;

/**
 * Created by laizuhong on 2015/10/28.
 */
public class Main extends AppCompatActivity {

    public static Main main;
    public static User user;
    DisplayImageOptions options;
    Toolbar toolbar;
    ActionBarDrawerToggle mDrawerToggle;
    TextView name;
    ImageView head;
    ListView listView;
//    ActionBar actionBar;
    WeiboFragment weiboFragment;
    SettingFragment settingFragment;
    UserWeiboFragment userWeiboFragment;
    FriendsFragment friendsFragment;
    FavoritFragment favoritFragment;
    private DrawerLayout mDrawerLayout = null;

    FrameLayout layout1,layout2;
    /**
     * 当前 Token 信息
     */
    private Oauth2AccessToken mAccessToken;
    /**
     * 用户信息接口
     */
    private UsersAPI mUsersAPI;
    /**
     * 微博 OpenAPI 回调接口。
     */
    private RequestListener mListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                MyLog.e("RequestListener user", response);
                // 调用 User#parse 将JSON串解析成User对象
                user = User.parse(response);
                if (user != null) {
                    name.setText(user.screen_name);
                    ImageLoader.getInstance().displayImage(user.avatar_large, head, options);
                } else {
                    Toast.makeText(Main.this, response, Toast.LENGTH_LONG).show();
                }
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            MyLog.e("RequestListener user", e.getMessage());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        main = this;
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar,R.string.drawer_open, R.string.drawer_close);
        mDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mDrawerToggle);
//        actionBar = getSupportActionBar();
//        actionBar.setDisplayShowCustomEnabled(true);
//        actionBar.setDisplayShowHomeEnabled(true);
//        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.setHomeAsUpIndicator(R.drawable.actionbar_menu);
        toolbar.setTitle("首页");





        name = (TextView) findViewById(R.id.name);
        head = (ImageView) findViewById(R.id.head);


        mAccessToken = AccessTokenKeeper.readAccessToken(this);
        mUsersAPI = new UsersAPI(this, Constants.APP_KEY, mAccessToken);
        long uid = Long.parseLong(mAccessToken.getUid());
        mUsersAPI.show(uid, mListener);
        listView = (ListView) findViewById(R.id.left_drawer);
        String[] menu = getResources().getStringArray(R.array.menu);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, menu);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switchFragment(position);
                mDrawerLayout.closeDrawer(Gravity.LEFT);//关闭抽屉
            }
        });

        head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(main, UserActivity.class);
                intent.putExtra("name", user.screen_name);
                mDrawerLayout.closeDrawer(Gravity.LEFT);
                startActivity(intent);
            }
        });

        findViewById(R.id.menu_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        layout1= (FrameLayout) findViewById(R.id.content_frame);
        layout2= (FrameLayout) findViewById(R.id.content_frame2);
        switchFragment(0);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        // 获取SearchView对象
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        if (searchView == null) {
            Log.e("SearchView", "Fail to get Search View.");
            return true;
        }
        // 配置SearchView的属性
        searchView.setIconifiedByDefault(true);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.e("onQueryTextSubmit", query);
                Intent intent = new Intent(main, SearchResultActvity.class);
                startActivity(intent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:// 点击返回图标事件
                if (mDrawerLayout.isDrawerVisible(Gravity.LEFT)) {
                    mDrawerLayout.closeDrawer(Gravity.LEFT);//关闭抽屉
                } else {
                    mDrawerLayout.openDrawer(Gravity.LEFT);//打开抽屉
                }
                break;
            case R.id.action_plus:
                Intent intent = new Intent(main, SendWeiboActivity.class);
                startActivity(intent);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
//            finish();
        }
        return super.onKeyDown(keyCode, event);


    }


    private void switchFragment(int tab) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        hideFragment(transaction);
        switch (tab) {
            case 0:
                layout1.setVisibility(View.VISIBLE);
                layout2.setVisibility(View.GONE);
                toolbar.setTitle("首页");
                if (weiboFragment == null) {
                    MyLog.e("weibofragment is null");
                    weiboFragment = new WeiboFragment();
                    transaction.add(R.id.content_frame, weiboFragment);
                } else {
                    MyLog.e("show weibofragment");
                    transaction.show(weiboFragment);
                }
                break;
            case 1:
                layout1.setVisibility(View.VISIBLE);
                layout2.setVisibility(View.GONE);
                toolbar.setTitle("我的微博");
                if (userWeiboFragment == null) {
                    userWeiboFragment = new UserWeiboFragment();
                    transaction.add(R.id.content_frame, userWeiboFragment);
                } else {
                    transaction.show(userWeiboFragment);
                }
                break;
            case 2:
                layout1.setVisibility(View.VISIBLE);
                layout2.setVisibility(View.GONE);
                toolbar.setTitle("我的关注");
                if (friendsFragment == null) {
                    friendsFragment = new FriendsFragment();
                    transaction.add(R.id.content_frame, friendsFragment);
                } else {
                    transaction.show(friendsFragment);
                }
                break;
            case 3:
                layout1.setVisibility(View.GONE);
                layout2.setVisibility(View.VISIBLE);
                toolbar.setTitle("我的收藏");
                if (favoritFragment == null) {
                    favoritFragment = new FavoritFragment();
                    transaction.replace(R.id.content_frame2, favoritFragment);
                } else {
                    transaction.show(favoritFragment);
                }
                break;
            case 4:
                layout1.setVisibility(View.VISIBLE);
                layout2.setVisibility(View.GONE);
                toolbar.setTitle("设置");
                if (settingFragment == null) {
                    settingFragment = new SettingFragment();
                    transaction.add(R.id.content_frame, settingFragment);
                } else {
                    transaction.show(settingFragment);
                }
                break;
        }
        transaction.commit();
    }


    private void hideFragment(FragmentTransaction transaction) {
        if (weiboFragment != null) {
            transaction.hide(weiboFragment);
        }
        if (userWeiboFragment != null) {
            transaction.hide(userWeiboFragment);
        }
        if (friendsFragment != null) {
            transaction.hide(friendsFragment);
        }
        if (settingFragment != null) {
            transaction.hide(settingFragment);
        }
        if (favoritFragment != null) {
            transaction.hide(favoritFragment);
        }
    }
}

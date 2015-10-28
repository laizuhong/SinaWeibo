package com.example.laizuhong.sinaweibo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.laizuhong.sinaweibo.config.AccessTokenKeeper;
import com.example.laizuhong.sinaweibo.fragment.WeiboFragment;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.UsersAPI;
import com.sina.weibo.sdk.openapi.models.ErrorInfo;
import com.sina.weibo.sdk.openapi.models.User;
import com.sina.weibo.sdk.utils.LogUtil;

/**
 * Created by laizuhong on 2015/10/28.
 */
public class Main extends AppCompatActivity {

    public static Main main;
    User user;
    DisplayImageOptions options;
    TextView name;
    ImageView head;
    ListView listView;
    ActionBar actionBar;
    private DrawerLayout mDrawerLayout = null;
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
                LogUtil.i("RequestListener user", response);
                // 调用 User#parse 将JSON串解析成User对象
                user = User.parse(response);
                if (user != null) {
                    name.setText(user.screen_name);
                    com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(user.avatar_large, head, options);
                } else {
                    Toast.makeText(Main.this, response, Toast.LENGTH_LONG).show();
                }
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            LogUtil.e("RequestListener user", e.getMessage());
            ErrorInfo info = ErrorInfo.parse(e.getMessage());
            Toast.makeText(Main.this, info.toString(), Toast.LENGTH_LONG).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);
        main = this;
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.actionbar_menu);
        actionBar.setTitle("主页");


        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, new WeiboFragment());
        transaction.addToBackStack(null);
        transaction.commit();


        name = (TextView) findViewById(R.id.name);
        head = (ImageView) findViewById(R.id.head);


        mAccessToken = AccessTokenKeeper.readAccessToken(this);
        mUsersAPI = new UsersAPI(this, com.example.laizuhong.sinaweibo.config.Constants.APP_KEY, mAccessToken);
        long uid = Long.parseLong(mAccessToken.getUid());
        mUsersAPI.show(uid, mListener);
        listView = (ListView) findViewById(R.id.left_drawer);
        String[] menu = getResources().getStringArray(R.array.menu);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, menu);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent = new Intent();
//                switch (position) {
//                    case 0:
//                        intent.setClass(main, SettingActivity.class);
//                        break;
//                    case 1:
//                        intent.setClass(main, SettingActivity.class);
//                        break;
//                    case 2:
//                        intent.setClass(main, SettingActivity.class);
//                        break;
//                    case 3:
//                        intent.setClass(main, SettingActivity.class);
//                        break;
//                }
                mDrawerLayout.closeDrawer(Gravity.LEFT);
//                startActivity(intent);
            }
        });

        head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(main, SettingActivity.class);
                mDrawerLayout.closeDrawer(Gravity.LEFT);
                startActivity(intent);
            }
        });

        findViewById(R.id.menu_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}

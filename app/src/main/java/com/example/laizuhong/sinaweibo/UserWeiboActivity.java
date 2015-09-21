package com.example.laizuhong.sinaweibo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;

import com.example.laizuhong.sinaweibo.config.AccessTokenKeeper;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.openapi.StatusesAPI;

/**
 * Created by laizuhong on 2015/9/16.
 */
public class UserWeiboActivity extends AppCompatActivity {

    ListView listView;
    /** 当前 Token 信息 */
    private Oauth2AccessToken mAccessToken;
    /** 用于获取微博信息流等操作的API */
    private StatusesAPI mStatusesAPI;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_all_weibo);
        init();
    }

    private void init(){
        listView= (ListView) findViewById(R.id.allweibo);
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        // 获取当前已保存过的 Token
        mAccessToken = AccessTokenKeeper.readAccessToken(this);
        // 对statusAPI实例化
        mStatusesAPI = new StatusesAPI(this, Constants.APP_KEY, mAccessToken);

    }
}

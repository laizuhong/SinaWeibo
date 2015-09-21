package com.example.laizuhong.sinaweibo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;

import com.example.laizuhong.sinaweibo.config.AccessTokenKeeper;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.LogoutAPI;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by laizuhong on 2015/9/16.
 */
public class SettingActivity extends AppCompatActivity{


    LogOutRequestListener mLogoutListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        getSupportActionBar().setTitle("设置");
        mLogoutListener=new LogOutRequestListener();
        findViewById(R.id.exit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new LogoutAPI(SettingActivity.this, Constants.APP_KEY,
                        AccessTokenKeeper.readAccessToken(SettingActivity.this)).logout(mLogoutListener);
            }
        });
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    /**
     * 登出按钮的监听器，接收登出处理结果。（API 请求结果的监听器）
     */
    private class LogOutRequestListener implements RequestListener {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                try {
                    JSONObject obj = new JSONObject(response);
                    String value = obj.getString("result");

                    if ("true".equalsIgnoreCase(value)) {
                        AccessTokenKeeper.clear(SettingActivity.this);
                        MainActivity.mainActivity.finish();
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
        }
    }
}

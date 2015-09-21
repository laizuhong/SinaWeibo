package com.example.laizuhong.sinaweibo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.laizuhong.sinaweibo.config.AccessTokenKeeper;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.StatusesAPI;
import com.sina.weibo.sdk.openapi.models.ErrorInfo;
import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.utils.LogUtil;

/**
 * Created by laizuhong on 2015/9/18.
 */
public class SendWeiboActivity extends AppCompatActivity implements View.OnClickListener{

    /** 当前 Token 信息 */
    private Oauth2AccessToken mAccessToken;
    /** 用于获取微博信息流等操作的API */
    private StatusesAPI mStatusesAPI;

    EditText edt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_weibo);
        init();
    }

    private void init(){
        findViewById(R.id.back).setOnClickListener(this);
        findViewById(R.id.fun1).setOnClickListener(this);
        findViewById(R.id.fun2).setOnClickListener(this);
        findViewById(R.id.fun3).setOnClickListener(this);
        findViewById(R.id.fun4).setOnClickListener(this);
        findViewById(R.id.fun5).setOnClickListener(this);
        findViewById(R.id.send).setOnClickListener(this);
        edt= (EditText) findViewById(R.id.text);
        // 获取当前已保存过的 Token
        mAccessToken = AccessTokenKeeper.readAccessToken(this);
        // 对statusAPI实例化
        mStatusesAPI = new StatusesAPI(this, Constants.APP_KEY, mAccessToken);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.fun1:
                Intent picture=new Intent(SendWeiboActivity.this,PictureActivity.class);
                startActivity(picture);
                break;
            case R.id.fun2:
                break;
            case R.id.fun3:
                break;
            case R.id.fun4:
                break;
            case R.id.fun5:
                break;
            case R.id.send:
                String text=edt.getText().toString();
                mStatusesAPI.update(text,"","",mListener);
                break;
        }
    }


    /**
     * 微博 OpenAPI 回调接口。
     */
    private RequestListener mListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            if (response.startsWith("{\"created_at\"")) {
                    // 调用 Status#parse 解析字符串成微博对象
                    Status status = Status.parse(response);
                    Toast.makeText(SendWeiboActivity.this,
                            "发送一送微博成功, id = " + status.id,
                            Toast.LENGTH_LONG).show();
                finish();
                } else {
                    Toast.makeText(SendWeiboActivity.this, response, Toast.LENGTH_LONG).show();
                }
            }

        @Override
        public void onWeiboException(WeiboException e) {
            LogUtil.e("onWeiboException", e.getMessage());
            ErrorInfo info = ErrorInfo.parse(e.getMessage());
            Toast.makeText(SendWeiboActivity.this, info.toString(), Toast.LENGTH_LONG).show();
        }
    };




}

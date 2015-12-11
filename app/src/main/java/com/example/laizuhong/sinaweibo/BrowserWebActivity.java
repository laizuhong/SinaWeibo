package com.example.laizuhong.sinaweibo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.laizuhong.sinaweibo.config.AccessTokenKeeper;
import com.example.laizuhong.sinaweibo.util.MyLog;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.legacy.ShortUrlAPI;
import com.sina.weibo.sdk.openapi.models.ErrorInfo;
import com.sina.weibo.sdk.utils.LogUtil;
import com.thefinestartist.finestwebview.FinestWebView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by laizuhong on 2015/11/10.
 */
public class BrowserWebActivity extends AppCompatActivity {
    String url;

    ShortUrlAPI shortUrlAPI;
    /**
     * 当前 Token 信息
     */
    private Oauth2AccessToken mAccessToken;


    /**
     * 微博 OpenAPI 回调接口。
     */
    private RequestListener mListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            MyLog.e(response);
            if (response.startsWith("{\"urls\"")) {
                try {
                    JSONObject object = new JSONObject(response);
                    JSONObject object1 = (JSONObject) object.getJSONArray("urls").get(0);
                    String long_url = object1.getString("url_long");
                    MyLog.e(long_url);
                    loadUrl(long_url);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                finish();
            } else {
                Toast.makeText(BrowserWebActivity.this, response, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            LogUtil.e("onWeiboException", e.getMessage());
            ErrorInfo info = ErrorInfo.parse(e.getMessage());
            Toast.makeText(BrowserWebActivity.this, info.toString(), Toast.LENGTH_LONG).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyLog.e(getIntent().getData() + "");
        setContentView(R.layout.activity_browser);
        url = getIntent().getData().toString().replace("erciyuan.", "");
        MyLog.e(url);
        if (url.contains("http")){
            // 获取当前已保存过的 Token
            mAccessToken = AccessTokenKeeper.readAccessToken(this);
            shortUrlAPI = new ShortUrlAPI(this, Constants.APP_KEY, mAccessToken);
            shortUrlAPI.expand(new String[]{url}, mListener);
        }else {
            MyLog.e("http://huati.weibo.com/k/"+url);
            loadUrl("http://huati.weibo.com/k/"+url.replace("topic://",""));
        }
    }

    private void loadUrl(String url){
        new FinestWebView.Builder(this)
                .theme(R.style.FinestWebViewTheme)
                .titleDefault("Bless This Stuff")
                .statusBarColorRes(R.color.redPrimaryDark)
                .toolbarColorRes(R.color.redPrimary)
                .titleColorRes(R.color.finestWhite)
                .urlColorRes(R.color.redPrimaryLight)
                .iconDefaultColorRes(R.color.finestWhite)
                .progressBarColorRes(R.color.finestWhite)
                .webViewBuiltInZoomControls(true)
                .webViewDisplayZoomControls(true)
                .swipeRefreshColorRes(R.color.redPrimaryDark)
                .menuColorRes(R.color.redPrimaryDark)
                .menuTextColorRes(R.color.finestWhite)
                .dividerHeight(0)
                .gradientDivider(false)
                .setCustomAnimations(R.anim.activity_open_enter, R.anim.activity_open_exit, R.anim.activity_close_enter, R.anim.activity_close_exit)
                .show(url);
    }

}

package com.example.laizuhong.sinaweibo;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.example.laizuhong.sinaweibo.config.AccessTokenKeeper;
import com.example.laizuhong.sinaweibo.util.MyLog;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.legacy.ShortUrlAPI;
import com.sina.weibo.sdk.openapi.models.ErrorInfo;
import com.sina.weibo.sdk.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by laizuhong on 2015/11/10.
 */
public class BrowserWebActivity extends BaseActivity {
    String url;
    WebView webView;
    ShortUrlAPI shortUrlAPI;
    ActionBar actionBar;
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
                    webView.loadUrl(long_url);
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
        actionBar = getSupportActionBar();
        init();
    }

    private void init() {
        webView = (WebView) findViewById(R.id.web);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);

        webView.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
                    webView.goBack();
                    return true;
                }
                return false;
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //webView.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });


        webView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                actionBar.setTitle(title);
            }

            @Override
            public View getVideoLoadingProgressView() {
                return super.getVideoLoadingProgressView();
            }
        });

        // 获取当前已保存过的 Token
        mAccessToken = AccessTokenKeeper.readAccessToken(this);

        shortUrlAPI = new ShortUrlAPI(this, Constants.APP_KEY, mAccessToken);
        //shortUrlAPI.expand(new String[]{url}, mListener);
        //webView.loadUrl("http://music.163.com/#/song?id=36308884&yyfrom=weibo");
    }


}

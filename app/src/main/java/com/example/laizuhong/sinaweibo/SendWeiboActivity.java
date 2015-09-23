package com.example.laizuhong.sinaweibo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.laizuhong.sinaweibo.bean.Picture;
import com.example.laizuhong.sinaweibo.config.AccessTokenKeeper;
import com.example.laizuhong.sinaweibo.util.PictureUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.StatusesAPI;
import com.sina.weibo.sdk.openapi.models.ErrorInfo;
import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.utils.LogUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by laizuhong on 2015/9/18.
 */
public class SendWeiboActivity extends AppCompatActivity implements View.OnClickListener{

    GridView gridView;
    PictureAdapter adapter;
    List<Picture> pictures;
    DisplayImageOptions options;
    EditText edt;
    /** 当前 Token 信息 */
    private Oauth2AccessToken mAccessToken;
    /** 用于获取微博信息流等操作的API */
    private StatusesAPI mStatusesAPI;
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
        adapter = new PictureAdapter();
        gridView = (GridView) findViewById(R.id.send_pic_grid);
        pictures = new ArrayList<>();
        gridView.setAdapter(adapter);
        edt= (EditText) findViewById(R.id.text);
        // 获取当前已保存过的 Token
        mAccessToken = AccessTokenKeeper.readAccessToken(this);
        // 对statusAPI实例化
        mStatusesAPI = new StatusesAPI(this, Constants.APP_KEY, mAccessToken);
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.logo)
                .showImageForEmptyUri(R.drawable.logo)
                .showImageOnFail(R.drawable.logo)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .displayer(new FadeInBitmapDisplayer(100)) // 展现方式：渐现
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.fun1:
                Intent picture=new Intent(SendWeiboActivity.this,PictureActivity.class);
                picture.putExtra("picture", (Serializable) pictures);
                startActivityForResult(picture, 1);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            List<Picture> item = (List<Picture>) data.getSerializableExtra("picture");
            if (item != null && item.size() > 0) {
                pictures.clear();
                pictures.addAll(item);
                if (pictures.size() < 9) {
                    pictures.add(new Picture("path"));
                }
                adapter.notifyDataSetChanged();
            }
        }
    }

    class PictureAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return pictures.size();
        }

        @Override
        public Object getItem(int position) {
            return pictures.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(SendWeiboActivity.this).inflate(R.layout.item_send_pic_grid, null);
            ImageView img = (ImageView) convertView.findViewById(R.id.imageview);
            ImageView delete = (ImageView) convertView.findViewById(R.id.delete);
            if (pictures.size() != 0 && getCount() < 9 && position == getCount() - 1) {
                img.setBackgroundResource(R.drawable.group_edit_member_add);
                img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (pictures.size() != 0 && getCount() < 9 && position == getCount() - 1) {
                            Intent picture = new Intent(SendWeiboActivity.this, PictureActivity.class);
                            picture.putExtra("picture", (Serializable) pictures);
                            startActivityForResult(picture, 1);
                        }
                    }
                });
                delete.setVisibility(View.GONE);
                return convertView;
            }

            final Picture picture = pictures.get(position);


            ImageLoader.getInstance().displayImage("file://" + picture.getPath(), img, options);
            delete.setTag(position);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int p = (int) v.getTag();
                    int group = pictures.get(p).getGroup();
                    int child = pictures.get(p).getChild();
                    Log.e("path", pictures.get(p).toString() + "   " + group + "   " + child);
                    pictures.remove(p);
                    PictureUtil.pictureFiles.get(group).getSets().get(child).isChecked = false;
                    adapter.notifyDataSetChanged();
                }
            });
            return convertView;
        }
    }

}

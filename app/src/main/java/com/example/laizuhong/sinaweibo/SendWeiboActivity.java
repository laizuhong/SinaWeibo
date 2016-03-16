package com.example.laizuhong.sinaweibo;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.laizuhong.sinaweibo.config.AccessTokenKeeper;
import com.example.laizuhong.sinaweibo.util.MyLog;
import com.example.laizuhong.sinaweibo.util.MyToast;
import com.example.laizuhong.sinaweibo.util.android.FriendsAPI;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.CommentsAPI;
import com.sina.weibo.sdk.openapi.StatusesAPI;
import com.sina.weibo.sdk.openapi.models.ErrorInfo;
import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.utils.LogUtil;

/**
 * Created by laizuhong on 2015/9/18.
 */
public class SendWeiboActivity extends AppCompatActivity implements View.OnClickListener {

    //    GridView gridView;
//    PictureAdapter adapter;
//    List<Picture> pictures;
    EditText edt;
    Status status;
    boolean is_comment;
    CardView share;
    TextView title, text;
    ImageView imageView, pic;
    String path;
    /** 当前 Token 信息 */
    private Oauth2AccessToken mAccessToken;
    /** 用于获取微博信息流等操作的API */
    private StatusesAPI mStatusesAPI;
    private FriendsAPI friendsAPI;
    private CommentsAPI commentsAPI;
    Toolbar toolbar;
    /**
     * 微博 OpenAPI 回调接口。
     */
    private RequestListener mListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            MyLog.e(response);
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
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("发微博");
        toolbar.setNavigationIcon(R.drawable.ic_menu_back);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setTitle("发微博");
        status = (Status) getIntent().getSerializableExtra("weibo");
        is_comment = getIntent().getBooleanExtra("comment", false);

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
        pic = (ImageView) findViewById(R.id.pic);
        pic.setOnClickListener(this);
//        adapter = new PictureAdapter();
//        gridView = (GridView) findViewById(R.id.send_pic_grid);
//        pictures = new ArrayList<>();
//        gridView.setAdapter(adapter);
        edt= (EditText) findViewById(R.id.text);
        // 获取当前已保存过的 Token
        mAccessToken = AccessTokenKeeper.readAccessToken(this);
        // 对statusAPI实例化
        mStatusesAPI = new StatusesAPI(this, Constants.APP_KEY, mAccessToken);
        friendsAPI = new FriendsAPI(this, Constants.APP_KEY, mAccessToken);
        commentsAPI = new CommentsAPI(this, Constants.APP_KEY, mAccessToken);

        share = (CardView) findViewById(R.id.share_layout);


        if (is_comment) {
            getSupportActionBar().setTitle("发评论");
        } else {
            if (status != null) {
                getSupportActionBar().setTitle("转发微博");
                share.setVisibility(View.VISIBLE);
                title = (TextView) findViewById(R.id.share_title);
                text = (TextView) findViewById(R.id.share_text);
                imageView = (ImageView) findViewById(R.id.share_image);
                ImageLoader.getInstance().displayImage(status.user.avatar_large, imageView, MyApp.options);
                title.setText(status.user.screen_name);
                text.setText(status.text);
            } else {
                share.setVisibility(View.GONE);
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_send, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.ok) {
            String text = edt.getText().toString();

            if (is_comment) {
                if (text == null) {
                    MyToast.makeText("请输入评论内容");
                }
                long id = Long.valueOf(status.id);
                commentsAPI.create(text, id, false, mListener);
                return true;
            }

            if (status != null) {
                if (text == null) {
                    text = "转发微博";
                }
                friendsAPI.shareWeibo(status.id, text, 0, mListener);
                return true;
            }

            if (text == null) {
                MyToast.makeText("请输入评论内容");
            }
            if (path == null) {
                mStatusesAPI.update(text, "", "", mListener);
            } else {
                Bitmap bitmap = ImageLoader.getInstance().loadImageSync("file://" + path, MyApp.options);
                mStatusesAPI.upload(text, bitmap, "", "", mListener);
            }
        }else if (item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.fun1:

                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, 0);

//                Intent picture=new Intent(SendWeiboActivity.this,PictureActivity.class);
//                if (pictures.size() > 0 && pictures.size() < 8) {
//                    pictures.remove(pictures.size() - 1);
//                }
//                picture.putExtra("picture", (Serializable) pictures);
//                startActivityForResult(picture, 1);
                break;
            case R.id.fun2:
                break;
            case R.id.fun3:
                break;
            case R.id.fun4:
                break;
            case R.id.fun5:
                break;
            case R.id.pic:
                AlertDialog.Builder builder = new AlertDialog.Builder(SendWeiboActivity.this);
                builder.setTitle("提示")
                        .setMessage("是否删除选择的图片？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                path = null;
                                pic.setVisibility(View.GONE);
                            }
                        }).setNegativeButton("取消", null).create().show();
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            path = cursor.getString(columnIndex);
            pic.setVisibility(View.VISIBLE);
            ImageLoader.getInstance().displayImage("file://" + path, pic, MyApp.options);
            //fun1.setImageBitmap(bitmap);
        }
    }


}

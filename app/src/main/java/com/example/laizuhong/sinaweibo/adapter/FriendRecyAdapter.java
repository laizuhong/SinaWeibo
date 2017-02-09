package com.example.laizuhong.sinaweibo.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.laizuhong.sinaweibo.MyApp;
import com.example.laizuhong.sinaweibo.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sina.weibo.sdk.openapi.models.User;

import java.util.List;

/**
 *
 * Created by laizuhong on 2015/10/30.
 */
public class FriendRecyAdapter extends BaseQuickAdapter<User> {


    public FriendRecyAdapter(Context context, List<User> data) {
        super(context, R.layout.item_friends, data);
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, User user) {
        ImageLoader.getInstance().displayImage(user.profile_image_url, (ImageView) baseViewHolder.getView(R.id.userhead), MyApp.options);
        baseViewHolder.setText(R.id.username,user.screen_name);
        baseViewHolder.setText(R.id.desc,user.description);
    }
}

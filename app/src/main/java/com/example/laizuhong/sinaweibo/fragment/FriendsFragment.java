package com.example.laizuhong.sinaweibo.fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.laizuhong.sinaweibo.Constants;
import com.example.laizuhong.sinaweibo.Main;
import com.example.laizuhong.sinaweibo.adapter.FriendAdapter;
import com.example.laizuhong.sinaweibo.util.MyToast;
import com.example.laizuhong.sinaweibo.util.android.FriendsAPI;
import com.sina.weibo.sdk.openapi.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by laizuhong on 2015/11/6.
 */
public class FriendsFragment extends BaseFragment  {


    int MODE;
    boolean fresh = false;
    FriendAdapter adapter;
    ArrayList<User> users;
    String uid;
    int page = -1;
    /**
     * 用于获取微博信息流等操作的API
     */
    private FriendsAPI friendsAPI;


    @Override
    public void complete(String response) {
        super.complete(response);
        if (!TextUtils.isEmpty(response)) {
            Log.e("RequestListener status", response);
            if (response.startsWith("{\"users\"")) {
                List<User> item = new ArrayList<>();
                try {
                    JSONObject object = new JSONObject(response);
                    page = object.getInt("next_cursor");
                    MyToast.makeText(page + "        " + object.getString("total_number"));
                    JSONArray array = object.getJSONArray("users");

                    for (int i = 0; i < array.length(); i++) {
                        item.add(User.parse(array.getString(i)));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (item != null && item.size() > 0) {
                    if (loading.getVisibility() == View.VISIBLE) {
                        loading.setVisibility(View.GONE);
                        ptrFrameLayout.setVisibility(View.VISIBLE);
                    }
                    ptrFrameLayout.refreshComplete();
                    if (MODE == 1) {
                        users.clear();
                    }
                    users.addAll(item);
                    setState(0);
                    adapter.notifyDataSetChanged();
                    fresh = false;
                }
            } else {
                Toast.makeText(context, response, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void update() {
        super.update();
        MODE = 1;
        page = 1;
        friendsAPI.getUserFriends(uid, 100, page, mListener);
    }

    public void init(View view) {
        super.init(view);
        uid = Main.user.id;
        users = new ArrayList<>();
        adapter = new FriendAdapter(context, users);
        listView.setAdapter(adapter);
        footview.setVisibility(View.GONE);


        // 对statusAPI实例化
        friendsAPI = new FriendsAPI(context, Constants.APP_KEY, mAccessToken);
        friendsAPI.getUserFriends(uid, 100, 0, mListener);

    }

    @Override
    public void loadMore(int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        super.loadMore(firstVisibleItem, visibleItemCount, totalItemCount);
        int count=totalItemCount-firstVisibleItem;
        if (count>0&&count<5&&fresh==false){
            if (page == 0) {
                return;
            }
            footview.setVisibility(View.VISIBLE);
            MODE = 2;
            fresh = true;
            setState(1);
            page++;
            friendsAPI.getUserFriends(uid, 100, page, mListener);
        }
    }
}

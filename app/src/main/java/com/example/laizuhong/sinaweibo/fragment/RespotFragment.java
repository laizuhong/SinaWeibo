package com.example.laizuhong.sinaweibo.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.laizuhong.sinaweibo.R;

/**
 * Created by laizuhong on 2015/9/30.
 */
public class RespotFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_repost, null);
        init(view);
        return view;
    }

    private void init(View view) {

    }
}

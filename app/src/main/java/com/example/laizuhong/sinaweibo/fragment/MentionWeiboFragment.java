package com.example.laizuhong.sinaweibo.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.laizuhong.sinaweibo.R;

/**
 * Created by laizuhong on 2015/11/10.
 */
public class MentionWeiboFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menttion, null);
        return view;
    }

    private void init(View view) {

    }
}

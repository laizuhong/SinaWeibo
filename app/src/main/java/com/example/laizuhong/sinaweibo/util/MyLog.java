package com.example.laizuhong.sinaweibo.util;

import android.util.Log;

public class MyLog {
    static String tag = "com.anhe.atc";

    public static void e(String tag, String text) {
        Log.e(tag, text);
    }

    public static void e(String text) {
        Log.e(tag, text);
    }

    public static void w(String tag, String text) {
        Log.w(tag, text);
    }

    public static void w(String text) {
        Log.w(tag, text);
    }

    public static void i(String tag, String text) {
        Log.i(tag, text);
    }

    public static void i(String text) {
        Log.i(tag, text);
    }
}

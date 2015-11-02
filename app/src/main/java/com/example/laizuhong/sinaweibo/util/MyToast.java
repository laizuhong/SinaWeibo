package com.example.laizuhong.sinaweibo.util;

import android.content.Context;
import android.widget.Toast;

import com.example.laizuhong.sinaweibo.MyApp;

import java.util.Timer;
import java.util.TimerTask;

public class MyToast {
    private static boolean canShow = true;

    public static void makeText(Context context, String text, int showTime) {
        if (canShow) {
            Toast.makeText(MyApp.getInstance(), text, showTime).show();
            canShow = false;
            new Timer().schedule(new TimerTask() {   //3�벻�ظ���
                public void run() {
                    canShow = true;
                }
            }, 2000);
        }
    }

    public static void makeText(Context context, String text) {
        if (canShow) {
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
            canShow = false;
            new Timer().schedule(new TimerTask() {   //3�벻�ظ���
                public void run() {
                    canShow = true;
                }
            }, 2000);
        }
    }


    public static void makeText(String text) {
        if (canShow) {
            Toast.makeText(MyApp.getInstance(), text, Toast.LENGTH_SHORT).show();
            canShow = false;
            new Timer().schedule(new TimerTask() {   //3�벻�ظ���
                public void run() {
                    canShow = true;
                }
            }, 2000);
        }
    }


}

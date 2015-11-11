package com.example.laizuhong.sinaweibo.util;

import android.os.Environment;

import java.io.File;

public class FileUtil {

    public static String LOCATION = Environment
            .getExternalStorageDirectory().getAbsolutePath() + "/sina/";
    public static String picture = LOCATION + "picture/";
    public static String bug = LOCATION + "bug/";

    public static void create() {
        MyLog.e("location", LOCATION);
        File file = new File(LOCATION);
        if (!file.exists()) {
            file.mkdirs();
        }


        File pic = new File(picture);
        if (!pic.exists()) {
            pic.mkdirs();
        }
        File bugFile = new File(bug);
        if (!bugFile.exists()) {
            bugFile.mkdirs();
        }
    }
}

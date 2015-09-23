package com.example.laizuhong.sinaweibo.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.example.laizuhong.sinaweibo.bean.Picture;
import com.example.laizuhong.sinaweibo.bean.PictureFile;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by laizuhong on 2015/9/18.
 */
public class PictureUtil {

    public static List<PictureFile> pictureFiles;
    private static ContentResolver contentResolver;

    private static PictureUtil instance;
    Context context;


    private PictureUtil(Context context) {
        this.context = context;
        contentResolver = context.getContentResolver();
    }

    public static PictureUtil newInstance(Context context) {
        if (instance == null) {
            instance = new PictureUtil(context);
            pictureFiles = getFolders();
        }
        return instance;
    }

    public static List<PictureFile> getFolders() {
        Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        Cursor mCursor = contentResolver.query(mImageUri, null,
                MediaStore.Images.Media.MIME_TYPE + "=? or "
                        + MediaStore.Images.Media.MIME_TYPE + "=?",
                new String[]{"image/jpeg", "image/png"},
                MediaStore.Images.Media.DATE_MODIFIED);
        HashMap<String, List<Picture>> map = capacity(mCursor);

        pictureFiles = new ArrayList<>();

        Set<Map.Entry<String, List<Picture>>> set = map.entrySet();
        for (Iterator<Map.Entry<String, List<Picture>>> iterator = set
                .iterator(); iterator.hasNext();) {
            Map.Entry<String, List<Picture>> entry = iterator.next();
            String parentName = entry.getKey();

            Picture b = entry.getValue().get(0);
            PictureFile tempPictureFile = new PictureFile(parentName, entry
                    .getValue().size() + 1, entry.getValue(), b.path);
            // 在第0个位置加入了拍照图片
            tempPictureFile.sets.add(0, new Picture());
            pictureFiles.add(tempPictureFile);
        }
        mCursor.close();
        return pictureFiles;
    }

    private static HashMap<String, List<Picture>> capacity(Cursor mCursor) {
        int group = 0;
        HashMap<String, List<Picture>> beans = new HashMap<>();
        while (mCursor.moveToNext()) {
            String path = mCursor.getString(mCursor
                    .getColumnIndex(MediaStore.Images.Media.DATA));

            long size = mCursor.getLong(mCursor
                    .getColumnIndex(MediaStore.Images.Media.SIZE));

            String display_name = mCursor.getString(mCursor
                    .getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));

            int id=mCursor.getInt(mCursor.getColumnIndex(MediaStore.Images.Media._ID));

            String parentName = new File(path).getParentFile().getName();
            List<Picture> sb;
            if (beans.containsKey(parentName)) {
                sb = beans.get(parentName);
                sb.add(new Picture(parentName, size, display_name, path,
                        false, id, sb.size(), sb.get(0).getGroup()));
            } else {
                sb = new ArrayList<>();

                sb.add(new Picture(parentName, size, display_name, path,
                        false, id, 0, group));
                group++;
            }
            beans.put(parentName, sb);
        }
        return beans;
    }

}

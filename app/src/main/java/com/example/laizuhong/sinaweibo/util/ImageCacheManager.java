package com.example.laizuhong.sinaweibo.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v4.util.LruCache;

import com.android.volley.toolbox.ImageLoader.ImageCache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;


public class ImageCacheManager implements ImageCache {

    public static Context context;
    static ImageCacheManager instance;
    String imagefile = Environment
            .getExternalStorageDirectory().getAbsolutePath() + "/khmall/";
    private LruCache<String, Bitmap> mCache;

    public ImageCacheManager() {
        int maxSize = 10 * 1024 * 1024;
        mCache = new LruCache<String, Bitmap>(maxSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight();
            }

        };
        File file = new File(imagefile);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public static ImageCacheManager getInstance(Context context) {

        ImageCacheManager.context = context;
        if (instance == null) {
            return instance = new ImageCacheManager();
        } else {
            return instance;
        }
    }

    public static void compressBmpToFile(Bitmap bmp, File file) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int options = 80;//个人喜欢从80开始,
        bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);
        while (baos.toByteArray().length / 1024 > 100) {
            baos.reset();
            options -= 10;
            bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);
        }
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Bitmap getBitmap(String arg0) {
        // TODO Auto-generated method stub

        File file = new File(imagefile + MD5.GetMD5Code(arg0) + ".pnga");
        if (!file.exists()) {


//			return BitmapFactory.decodeResource(context.getResources(), R.drawable.item_default_image);

            return mCache.get(arg0);

        }
        Bitmap bitmap = BitmapFactory.decodeFile(imagefile + MD5.GetMD5Code(arg0) + ".pnga");

        if (bitmap != null) {
            return bitmap;
        }
        return mCache.get(arg0);


    }

//	public void saveMyBitmap(String bitName, Bitmap mBitmap) throws IOException {
//		File f = new File(imagefile + MD5.GetMD5Code(bitName) + ".pnga");
//		f.createNewFile();
//		FileOutputStream fOut = null;
//		try {
//			fOut = new FileOutputStream(f);
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
//		mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
//		try {
//			fOut.flush();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		try {
//			fOut.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}

    @Override
    public void putBitmap(final String arg0, final Bitmap arg1) {
        // TODO Auto-generated method stub

        if (arg1 == null) {

        } else {

            new Thread() {
                public void run() {
                    compressBmpToFile(arg1, new File(imagefile + MD5.GetMD5Code(arg0) + ".pnga"));
                }
            }.start();


        }
    }

    private Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 90;
        //MyLog.e("baos.tobytearray.length", baos.toByteArray().length/1024+"");
        while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();// 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;// 每次都减少10
        }
        //	MyLog.e("baos.tobytearray.length", baos.toByteArray().length/1024+"");

        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
        return bitmap;
    }
}

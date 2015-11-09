package com.example.laizuhong.sinaweibo.util;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by laizuhong on 2015/10/29.
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {

    public static final String TAG = "bug";
    // CrashHandler实例
    private static CrashHandler INSTANCE = new CrashHandler();
    TelephonyManager manager;
    File file = null;
    // 系统默认的UncaughtException处理类
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    // 程序的Context对象
    private Context mContext;
    // 用来存储设备信息和异常信息
    private Map<String, String> infos = new HashMap<String, String>();
    // 用于格式化日期,作为日志文件名的一部分
    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

    /**
     * 保证只有一个CrashHandler实例
     */
    private CrashHandler() {
    }

    /**
     * 获取CrashHandler实例 ,单例模式
     */
    public static CrashHandler getInstance() {
        return INSTANCE;
    }

    /**
     * 初始化
     *
     * @param context
     */
    public void init(Context context) {
        mContext = context;
        // 获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();

        manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        // 设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        MyLog.e(TAG, "error : " + ex.getMessage());
        if (!handleException(ex) && mDefaultHandler != null) {
            // 如果用户没有处理则让系统默认的异常处理器来处理

            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
            }


            mDefaultHandler.uncaughtException(thread, ex);


            // 退出程序
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }


    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private boolean handleException(final Throwable ex) {
        if (ex == null) {
            return false;
        }
        // 使用Toast来显示异常信息
//        new Thread() {
//            @Override
//            public void run() {
//                Looper.prepare();
//
//
//
//
//                Toast.makeText(mContext, "很抱歉,程序出现异常,即将退出.!!!!!",
//                        Toast.LENGTH_LONG).show();
//
//                PathUtils.create();
//                saveCrashInfo2File(ex);
//                new ApicDao().upLoadLog(file, ContextUtil.loginUser.getUid());
//
//                Looper.loop();
//            }
//        }.start();
//        // 收集设备参数信息
//        collectDeviceInfo(mContext);
        saveCrashInfo2File(ex);
//        XmppConnection.closeConnection();
//        NotiCtrler.cancelNoti();
//        MyLogCat.plint("xxxxxxxxxxxxxxx", "com.wtstudio.together.activities.TogetherService");
//        Intent intent=new Intent("com.wtstudio.together.activities.TogetherService");
//        mContext.stopService(intent);

//		new Thread(){
//			public void run() {
//			};
//		}.start();
        // 保存日志文件
//        try {
//            Thread.sleep(500);
//        } catch (InterruptedException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        Log.i(TAG, "ex:" + ex.toString() + "--" + ex.getLocalizedMessage());
//        android.os.Process.killProcess(android.os.Process.myPid());
//        System.exit(1);
        return true;
    }

    /**
     * 收集设备参数信息
     *
     * @param ctx
     */
    public void collectDeviceInfo(Context ctx) {
//        try {
//            PackageManager pm = ctx.getPackageManager();
//            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(),
//                    PackageManager.GET_ACTIVITIES);
//            if (pi != null) {
//                String versionName = pi.versionName == null ? "null"
//                        : pi.versionName;
//
//                String versionCode = pi.versionCode + "";
//                infos.put("versionName", versionName);
//                infos.put("versionCode", versionCode);
//            }
//
//
//
//
//
//        } catch (NameNotFoundException e) {
//            MyLog.e(TAG, "an error occured when collect package info", e);
//        }
//        Field[] fields = Build.class.getDeclaredFields();
//        for (Field field : fields) {
//            try {
//                field.setAccessible(true);
//            } catch (Exception e) {
//                MyLog.e(TAG, "an error occured when collect crash info", e);
//            }
//        }
    }


    private String getInfo() {
        // TelephonyManager mTm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        String phoneInfo = "BOARD: " + android.os.Build.BOARD + "\n";
        //VERSION.RELEASE 固件版本
        phoneInfo += ", VERSION.RELEASE: " + android.os.Build.VERSION.RELEASE + "\n";
        phoneInfo += ", VERSION.CODENAME: " + android.os.Build.VERSION.CODENAME + "\n";

        phoneInfo += ",DEVICDID" + manager.getDeviceId() + "\n";  //系统唯一标志
        phoneInfo += ",subscriberid" + manager.getSubscriberId() + "\n";   //取出IMSI
        phoneInfo += ",LINE1NUMBER" + manager.getLine1Number() + "\n"; // 手机号码，有的可得，有的不可得
        //BOARD 主板
        phoneInfo += ", BOOTLOADER: " + android.os.Build.BOOTLOADER + "\n";
        //BRAND 运营商
        phoneInfo += ", BRAND: " + android.os.Build.BRAND + "\n";
        phoneInfo += ", CPU_ABI: " + android.os.Build.CPU_ABI + "\n";
        phoneInfo += ", CPU_ABI2: " + android.os.Build.CPU_ABI2 + "\n";
        //DEVICE 驱动
        phoneInfo += ", DEVICE: " + android.os.Build.DEVICE + "\n";
        //DISPLAY Rom的名字 例如 Flyme 1.1.2（魅族rom）  JWR66V（Android nexus系列原生4.3rom）
        phoneInfo += ", DISPLAY: " + android.os.Build.DISPLAY + "\n";
        //指纹
        phoneInfo += ", FINGERPRINT: " + android.os.Build.FINGERPRINT + "\n";
        //HARDWARE 硬件
        phoneInfo += ", HARDWARE: " + android.os.Build.HARDWARE + "\n";
        phoneInfo += ", HOST: " + android.os.Build.HOST + "\n";
        phoneInfo += ", ID: " + android.os.Build.ID + "\n";
        //MANUFACTURER 生产厂家
        phoneInfo += ", MANUFACTURER: " + android.os.Build.MANUFACTURER + "\n";
        //MODEL 机型
        phoneInfo += ", MODEL: " + android.os.Build.MODEL + "\n";
        phoneInfo += ", PRODUCT: " + android.os.Build.PRODUCT + "\n";
        phoneInfo += ", RADIO: " + android.os.Build.RADIO + "\n";
        phoneInfo += ", RADITAGSO: " + android.os.Build.TAGS + "\n";
        phoneInfo += ", TIME: " + android.os.Build.TIME + "\n";
        phoneInfo += ", TYPE: " + android.os.Build.TYPE + "\n";
        phoneInfo += ", USER: " + android.os.Build.USER + "\n";
        //VERSION.INCREMENTAL 基带版本
        phoneInfo += ", VERSION.INCREMENTAL: " + android.os.Build.VERSION.INCREMENTAL + "\n";
        //VERSION.SDK SDK版本
        phoneInfo += ", VERSION.SDK: " + android.os.Build.VERSION.SDK + "\n";
        phoneInfo += ", VERSION.SDK_INT: " + android.os.Build.VERSION.SDK_INT + "\n";
        return phoneInfo;
    }


    public String fetch_status() {
//		TelephonyManager tm = (TelephonyManager) this
//				.getSystemService(Context.TELEPHONY_SERVICE);//

        String str = "";
        str += "DeviceId(IMEI) = " + manager.getDeviceId() + "\n";
        str += "DeviceSoftwareVersion = " + manager.getDeviceSoftwareVersion()
                + "\n";
        str += "Line1Number = " + manager.getLine1Number() + "\n";
        str += "NetworkCountryIso = " + manager.getNetworkCountryIso() + "\n";
        str += "NetworkOperator = " + manager.getNetworkOperator() + "\n";
        str += "NetworkOperatorName = " + manager.getNetworkOperatorName() + "\n";
        str += "NetworkType = " + manager.getNetworkType() + "\n";
        str += "honeType = " + manager.getPhoneType() + "\n";
        str += "SimCountryIso = " + manager.getSimCountryIso() + "\n";
        str += "SimOperator = " + manager.getSimOperator() + "\n";
        str += "SimOperatorName = " + manager.getSimOperatorName() + "\n";
        str += "SimSerialNumber = " + manager.getSimSerialNumber() + "\n";
        str += "SimState = " + manager.getSimState() + "\n";
        str += "SubscriberId(IMSI) = " + manager.getSubscriberId() + "\n";
        str += "VoiceMailNumber = " + manager.getVoiceMailNumber() + "\n";
        //text.setText(str);
        return str;
    }

    //获取CPU信息
    public String[] getCpuInfo() {
        String str1 = "/proc/cpuinfo";
        String str2 = "";
        String[] cpuInfo = {"", ""};  //1-cpu型号  //2-cpu频率
        String[] arrayOfString;
        try {
            FileReader fr = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(fr, 8192);
            str2 = localBufferedReader.readLine();
            arrayOfString = str2.split("\\s+");
            for (int i = 2; i < arrayOfString.length; i++) {
                cpuInfo[0] = cpuInfo[0] + arrayOfString[i] + " ";
            }
            str2 = localBufferedReader.readLine();
            arrayOfString = str2.split("\\s+");
            cpuInfo[1] += arrayOfString[2];
            localBufferedReader.close();
        } catch (IOException e) {
        }
//        Log.i(TAG, "cpuinfo:" + cpuInfo[0] + " " + cpuInfo[1]);
        return cpuInfo;
    }


    /**
     * 保存错误信息到文件中
     *
     * @param ex
     * @return 返回文件名称, 便于将文件传送到服务器
     */
    private String saveCrashInfo2File(Throwable ex) {

        StringBuffer sb = new StringBuffer();
        // for (Map.Entry<String, String> entry : infos.entrySet())
        // {
        // String key = entry.getKey();
        // String value = entry.getValue();
        // sb.append(key + "=" + value + "\n");
        // }

        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            Log.e(TAG, "cause:" + cause.toString() + "--");
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        Log.e(TAG, "result:" + result);
        sb.append(result);
        //sb.append(fetch_status());
        sb.append(getInfo());

//        try {
//            //long timestamp = System.currentTimeMillis();
//            String time = formatter.format(new Date());
//            String fileName = "crash-" + time + "-" + ContextUtil.loginUser.getUid() + ".log";
//            if (ConstantUtils.isOnline) {
//                fileName = "crash-online.log";
//            }
//            String path = PathUtils.BUGPATH;
//            file=new File(path+fileName);
//            if (!file.exists()) {
//                file.createNewFile();
//            }
//            FileOutputStream fos = new FileOutputStream(path + fileName);
//            fos.write(sb.toString().getBytes());
//            fos.close();
//
//            return fileName;
//        } catch (Exception e) {
//            MyLog.e(TAG, "an error occured while writing file...", e);
//        }
        return null;
    }

    class ConstantUtils {

        /***
         * 是否上线版本
         ***/
        public final static boolean isOnline = false;
        /***
         * 时间格式
         */
        public static final String timeFormat = "yyyy-MM-dd HH:mm:ss";

    }
}

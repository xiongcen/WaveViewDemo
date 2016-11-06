
package com.example.xiongcen.myapplication.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Process;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * <br/>
 * 系统工具类. <br/>
 * 主要用于获取系统信息,如设备ID、操作系统版本等
 *
 * @author wjying
 */
public class SystemUtils {

    static final String TAG = SystemUtils.class.getName();
    /**
     * 默认imei
     */
    static final String DEFAULT_IMEI = "000000000000000";

    /**
     * 获取android id
     *
     * @param context
     * @return
     */
    public static String getAndroidId(Context context) {
        if (isEmulator(context)) {
            return null;
        }
        try {
            return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch (Exception e) {

        }
        return null;
    }

    /**
     * 是否是模拟器
     *
     * @param context
     * @return
     */
    public static boolean isEmulator(Context context) {
        String imei = getIMEI(context);
        if ("000000000000000".equals(imei)) {
            return true;
        }
        return (Build.MODEL.equalsIgnoreCase("sdk")) || (Build.MODEL.equalsIgnoreCase("google_sdk")) || Build.BRAND.equalsIgnoreCase("generic");
    }

    /**
     * 当前是否横屏
     *
     * @param context
     * @return
     */
    public static boolean isLandscape(Context context) {
        return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    /**
     * 基于Android3.0的平板
     *
     * @param context
     * @return
     */
    public static boolean isHoneycombTablet(Context context) {
        return isHoneycomb() && isTablet(context);
    }

    /**
     * 系统为Android5.0
     *
     * @return
     */
    public static boolean isLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static boolean isAutoRotationOn(Context ctx) {
        return Settings.System.getInt(ctx.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0) == 1;
    }

    /**
     * 系统为Android4.4,增加沉浸模式
     *
     * @return
     */
    public static boolean isKitkat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    /**
     * 系统为Android3.0
     *
     * @return
     */
    public static boolean isHoneycomb() {
        // Can use static final constants like HONEYCOMB, declared in later versions
        // of the OS since they are inlined at compile time. This is guaranteed behavior.
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    /**
     * 系统为Android2.1.x
     *
     * @return
     */
    public static boolean isEclair_MR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR_MR1;
    }

    /**
     * 判断是否为平板设备
     *
     * @param context
     * @return
     */
    public static boolean isTablet(Context context) {
        //暂时屏蔽平板
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            return context.getResources().getConfiguration().smallestScreenWidthDp >= 600;
//    		return (context.getResources().getConfiguration().screenLayout
//                    & Configuration.SCREENLAYOUT_SIZE_MASK)
//                    >= Configuration.SCREENLAYOUT_SIZE_LARGE;
        }
        return false;
    }

    /**
     * 获取mac地址
     *
     * @param context
     * @return
     */
    public static String getMac(Context context) {
        try {
            WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wifi.getConnectionInfo();
            String mac = info.getMacAddress();
            return TextUtils.isEmpty(mac) ? "" : mac;
        } catch (Exception e) {

        }
        return "";
    }

    public static String getIMEI(Context context) {
        try {
            TelephonyManager tm = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            String imei = tm.getDeviceId();
            return imei;
        } catch (Exception ioe) {
        }
        return null;
    }

    /**
     * 获取设备名称.
     *
     * @return
     */
    public static String getBuildModel() {
        return android.os.Build.MODEL;
    }

    /**
     * 获取操作系统类型.
     *
     * @return
     */
    public static String getBuildOS() {
        return Build.DISPLAY;
    }

    /**
     * 获取ROM制造商
     *
     * @return
     */
    public static String getBuildManufacturer() {
        return Build.MANUFACTURER;
    }

    /**
     * 获取设备品牌.
     *
     * @return
     */
    public static String getBuildBrand() {
        return Build.BRAND;
    }

    /**
     * 获取设备SDK版本号.
     *
     * @return
     */
    public static int getBuildVersionSDK() {
        return android.os.Build.VERSION.SDK_INT;
    }

    /**
     * 获取设备系统版本号.
     *
     * @return
     */
    public static String getBuildVersionRelease() {
        return android.os.Build.VERSION.RELEASE;
    }

    /**
     * 判断SD卡是否插入 即是否有SD卡
     */
    public static boolean isSDCardMounted() {
        return android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment
                .getExternalStorageState());
    }

    /**
     * 是否：已经挂载,但只拥有可读权限
     */
    public static boolean isSDCardMountedReadOnly() {
        return android.os.Environment.MEDIA_MOUNTED_READ_ONLY.equals(android.os.Environment
                .getExternalStorageState());
    }

    /**
     * 获取android当前可用内存大小
     */
    public static String getAvailMemory(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        MemoryInfo mi = new MemoryInfo();
        am.getMemoryInfo(mi);
        // mi.availMem; 当前系统的可用内存

        return Formatter.formatFileSize(context, mi.availMem);// 将获取的内存大小规格化
    }

    /**
     * 获取屏幕的亮度
     */
    public static int getScreenBrightness(Activity activity) {
        int nowBrightnessValue = 0;
        ContentResolver resolver = activity.getContentResolver();
        try {
            nowBrightnessValue = android.provider.Settings.System.getInt(resolver,
                    Settings.System.SCREEN_BRIGHTNESS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nowBrightnessValue;
    }

    /**
     * 获取手机屏幕的宽和高
     *
     * @param c
     * @return map("w", width) map("h",height);
     */
    public static HashMap<String, Integer> getWidth_Height(Context c) {
        DisplayMetrics metrics = c.getApplicationContext().getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        HashMap<String, Integer> m = new HashMap<String, Integer>();
        m.put("w", width);
        m.put("h", height);
        return m;
    }

    /**
     * 获取平板在横屏时webview的宽度
     *
     * @param c
     * @return
     */
    public static int getTabletWebViewWidth(Context c) {
        // 0.82f根据当前webview的padding计算得来
        return (int) ((float) SystemUtils.getScreenWidth(c) * 0.82f / c.getResources().getDisplayMetrics().density);
    }

    /**
     * 获取手机屏幕的宽和高size wxh
     *
     * @param c
     * @return width X height
     */
    public static String getWidthXHeight(Context c) {
        Map<String, Integer> m = getWidth_Height(c);
        String size = m.get("w") + "x" + m.get("h");
        return size;
    }

    /**
     * 获取手机分辨率宽度大小
     *
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
        DisplayMetrics dm = context.getApplicationContext().getResources().getDisplayMetrics();
        return dm.widthPixels;
    }

    /**
     * 获取手机分辨率长度大小
     *
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        DisplayMetrics dm = context.getApplicationContext().getResources().getDisplayMetrics();
        return dm.heightPixels;
    }

    /**
     * 获取手机屏幕密度
     *
     * @param context
     * @return
     */
    public static float getScreenDensity(Context context) {
        DisplayMetrics dm = context.getApplicationContext().getResources().getDisplayMetrics();
        return dm.density;
    }

    /**
     * 4.0+获取虚拟导航高度
     *
     * @param context
     * @return
     */
    public static int getNavigationBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    /**
     * 获取手机状态栏高度
     *
     * @param activity
     * @return
     */
    public static int getStatusBarHeight(Activity activity) {
        Rect rect = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        return rect.top;
    }

    /**
     * 获取屏幕内容视图的高度（屏幕高度 - 系统状态栏高度 - 虚拟导航条高度）
     *
     * @return
     */
    public static int getScreenContentHeight(Activity activity) {
        int screenHeight = getScreenHeight(activity);
        int statusBarHeight = getStatusBarHeight(activity);
        int naviHeight = getNavigationBarHeight(activity);
        return screenHeight - statusBarHeight - naviHeight;
    }

    /**
     * 获取应用窗口高度
     *
     * @return
     */
    public static int getAppWindowHeight(Activity activity) {
        Rect rect = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        return rect.bottom - rect.top;
    }

    /**
     * 获得设备html宽度
     *
     * @param context
     * @return
     */
    public static int getDeviceHtmlWidth(Context context) {

        if (isHoneycombTablet(context) && isLandscape(context)) {
            return getTabletWebViewWidth(context);
        }

        int width = (int) (getScreenWidth(context) / context.getResources().getDisplayMetrics().density);

        return width;
    }

    /**
     * 得到dimen定义的大小
     *
     * @param context
     * @param dimenId
     * @return
     */
    public static int getDimension(Context context, int dimenId) {
        return (int) context.getResources().getDimension(dimenId);
    }

    /**
     * 判断应用是否安装
     *
     * @param context
     * @param appName
     * @return
     */
    public static boolean isAppInstalled(Context context, String appName) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(appName, 0);
            if (null != packageInfo) {
                return true;
            }
        } catch (NameNotFoundException e) {
        }
        return false;
    }

    /**
     * 返回应用版本号
     *
     * @param context
     * @return
     */
    public static String getAppVersion(Context context) {
        return getAppVersion(context, context.getPackageName());
    }

    /**
     * 返回应用版本号
     *
     * @param context
     * @return
     */
    public static int getAppVersionCode(Context context) {
        return getAppVersionCode(context, context.getPackageName());
    }

    /**
     * 根据packageName包名的应用获取应用版本名称,如未安装返回null
     *
     * @param context
     * @param packageName
     * @return
     */
    public static String getAppVersion(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return null;
        }
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
            if (info != null) {
                return info.versionName;
            }
        } catch (NameNotFoundException e) {
        }
        return null;
    }

    /**
     * 根据packageName包名的应用获取应用版本名称,如未安装返回null
     *
     * @param context
     * @param packageName
     * @return
     */
    public static int getAppVersionCode(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return 0;
        }
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
            return info.versionCode;
        } catch (NameNotFoundException e) {
            return 0;
        }
    }

    /**
     * 根据packageName包名的应用获取应用信息,如未安装返回null
     *
     * @param context
     * @param packageName
     * @return
     */
    public static PackageInfo getAppInfo(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return null;
        }
        try {
            return context.getPackageManager().getPackageInfo(packageName, 0);
        } catch (NameNotFoundException e) {
            return null;
        }
    }

    /**
     * 判断打开新闻，按返回键是否要回到列表页面
     * 1、网易新闻内部点击链接：不回到列表页
     * 2、外部浏览器点击链接：不回到列表
     * 3、uri参数用来判断某些特殊uri条件需要返回原界面的情形
     */
    public static boolean shouldStartMain(Context context, Uri uri) {
        try {
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<RunningTaskInfo> tasksInfoList = am.getRunningTasks(1);

            if (tasksInfoList == null || tasksInfoList.size() == 0) {
                return false;
            }

            String appName = context.getPackageName();
            RunningTaskInfo taskInfo = tasksInfoList.get(0);
            if (taskInfo.numActivities == 1 || !appName.equals(taskInfo.baseActivity.getPackageName())) {
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    /**
     * 判断是不是合法时间
     *
     * @return
     */
    public static boolean isValidTime(String startTime, String endTime) {
        if (TextUtils.isEmpty(startTime) || TextUtils.isEmpty(endTime)) {
            return false;
        }
        return isValidTime("yyyy-MM-dd HH:mm:ss", "Asia/Shanghai", startTime, endTime);
    }

    /**
     * 判断是不是合法时间
     *
     * @return
     */
    public static boolean isValidTime(String format, String timeZone, String startTime, String endTime) {
        try {
            SimpleDateFormat df = new SimpleDateFormat(format);
            TimeZone timeZoneshanghai = TimeZone.getTimeZone(timeZone);
            df.setTimeZone(timeZoneshanghai);

            Date startDate = df.parse(startTime);
            Date endDate = df.parse(endTime);
            long start = startDate.getTime();
            long end = endDate.getTime();

            long now = System.currentTimeMillis();
            if (now > start && now < end) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 判断是不是合法时间
     *
     * @return
     */
    public static boolean isValidTime(String endTime) {
        if (TextUtils.isEmpty(endTime)) {
            return false;
        }
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            TimeZone timeZoneshanghai = TimeZone.getTimeZone("Asia/Shanghai");
            df.setTimeZone(timeZoneshanghai);

            Date endDate = df.parse(endTime);
            long end = endDate.getTime();

            long now = System.currentTimeMillis();
            if (now < end) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public static boolean isX86Cpu() {
        return "x86_none".equals(CpuInfoUtils.getCpuType());
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static void setImmersiveMode(boolean enable, View view) {
        if (view == null) {
            return;
        }
        if (!isKitkat()) {
            return;
        }
        if (enable) {
            view.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                            | View.SYSTEM_UI_FLAG_IMMERSIVE);
        } else {
            view.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
    }

    /**
     * 获取当前进程名
     *
     * @param context
     * @return
     */
    public static String getProcessName(Context context) {
        String processName = null;
        // 获取ActivityManager
        ActivityManager am = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE));

        int count = 0;
        while (true) {
            for (ActivityManager.RunningAppProcessInfo info : am.getRunningAppProcesses()) {
                if (info.pid == Process.myPid()) {
                    processName = info.processName;

                    NeteaseLog.i(TAG, "processName is " + info.processName);
                    break;
                }
            }

            // 返回进程名
            if (!TextUtils.isEmpty(processName)) {
                return processName;
            }


            NeteaseLog.e(TAG, "getProcessName count is: " + count);
            // Take a rest and again
            try {
                // 最多执行3次
                if (count > 3) {

                    NeteaseLog.e(TAG, "getProcessName count > 3 " + Thread.currentThread().getName());
                    return context.getPackageName();
                }
                Thread.sleep(10L);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            count++;
        }
    }


    public static boolean isMainProcess(Context context) {
        String packageName = context.getPackageName();
        String processName = getProcessName(context);
        if (packageName.equals(processName)) {
            return true;
        }

        return false;
    }

    public static float dp2px(Resources resources, float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
    }

    /**
     * 打开绝对路径下的app安装界面
     *
     * @param mApkPath
     */
    public static void openInstallApp(Context context, String mApkPath) {
        if (TextUtils.isEmpty(mApkPath) || context == null)
            return;
        File file = new File(mApkPath);
        if (file.exists()) {
            //直接打开安装界面
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse("file://" + mApkPath), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    /**
     * 获取已经安装应用的名称和报名，返回数据为packageName:appName的列表
     *
     * @param context
     * @return
     */
    public static List<CharSequence> getInstalledAppList(Context context) {
        List<CharSequence> resultList = new ArrayList<>();
        PackageManager packageManager = context.getPackageManager();
        List<ApplicationInfo> installedApplications = packageManager.getInstalledApplications(0);
        for (int i = 0; i < installedApplications.size(); i++) {
            ApplicationInfo applicationInfo = installedApplications.get(i);
            if (applicationInfo != null) {
                String packageName = applicationInfo.packageName;
                CharSequence appName = applicationInfo.loadLabel(packageManager);
                if (!TextUtils.isEmpty(packageName) && !TextUtils.isEmpty(appName)) {
                    resultList.add(packageName + ":" + appName);
                }
            }

        }
        return resultList;
    }


    /**
     * 获取ip地址
     *
     * @return ip地址字符串
     */
    public static String getIpAddress() {
        String ipAddress = null;
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        ipAddress = inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            return null;
        }
        Log.d("", "ip address:" + ipAddress);
        return ipAddress;
    }


    public static String getCurrentOperator(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getNetworkOperator();
    }


    static public void sendEmail(Context context, String emailAccount, String content, String filePath) {
        Intent email = new Intent(Intent.ACTION_SEND);

        String[] emailReciver = new String[]{emailAccount};
        email.putExtra(Intent.EXTRA_EMAIL, emailReciver);
        email.putExtra(Intent.EXTRA_SUBJECT,
                "用户反馈");
        email.putExtra(Intent.EXTRA_TEXT, content);
        if (!TextUtils.isEmpty(filePath)) {
            email.setType("application/extension");
            email.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(filePath)));
        }

        context.startActivity(Intent.createChooser(email,
                "发送邮件"));
    }


    /**
     * 将应用版本String转化为Float
     * 例如："5.6.2"->5.6 & "6.0"->6.0
     *
     * @param version
     * @return
     */
    public static float versionTransform(String version) {
        float versionF = 0f;
        if (TextUtils.isEmpty(version)) {
            return versionF;
        }
        String[] split = version.split("\\.");
        if (split == null) {
            return versionF;
        }

        try {
            if (split.length == 2) {
                versionF = Float.parseFloat(version);
            } else if (split.length == 3) {
                versionF = Float.parseFloat(split[0] + "." + split[1]);
            }
        } catch (Exception e) {
        }

        NeteaseLog.i("AppVersionModel", "versionF=" + versionF);

        return versionF;
    }

    public static int[] getDrawableArray(Context context, int resId) {
        int[] array = null;
        try {
            TypedArray ar = context.getResources().obtainTypedArray(resId);
            int len = ar.length();
            array = new int[len];
            for (int i = 0; i < len; i++) {
                array[i] = ar.getResourceId(i, 0);
            }
            ar.recycle();
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
        return array;
    }


}

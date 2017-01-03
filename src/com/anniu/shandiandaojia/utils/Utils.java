package com.anniu.shandiandaojia.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;

import com.anniu.shandiandaojia.app.App;
import com.anniu.shandiandaojia.base.ActivityMgr;
import com.anniu.shandiandaojia.db.jsondb.PrepayIdInfo;
import com.anniu.shandiandaojia.task.AppUpgradeService;
import com.anniu.shandiandaojia.view.MyDialog;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zxl
 * @ClassName: Utils
 * @Description: 应用程序全局变量
 * @date 2014年10月27日 上午10:46:56
 */
@SuppressLint("SimpleDateFormat")
public class Utils {

    public static final String VERSION_NAME = "versionName";
    public static final String VERSION_CODE = "versionCode";
    /**
     * 客户端版本号，用于升级的，从androidManiferst取
     */
    public static String version_name;
    /**
     * 是否是测试环境
     */
    public static boolean IS_TEST;

    /***
     * 获取手机型号
     */
    public static String getModel() {
        return android.os.Build.MODEL;
    }

    /**
     * 获取操作系统的版本号
     *
     * @return String 系统版本号
     */
    public static String getSysRelease() {
        return android.os.Build.VERSION.RELEASE;
    }

    /**
     * 初始化
     *
     * @param context
     */
    @SuppressWarnings("unused")
    public static void init(Context context) {
        PackageManager manager = context.getPackageManager();
        PackageInfo info;
        ApplicationInfo appInfo;
        try {
            info = manager.getPackageInfo(context.getPackageName(), 0);
            appInfo = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);
            version_name = info.versionName;
        } catch (NameNotFoundException e) {
            MyLog.e("init", e.toString());
        }
        // 初始化对话框
        DialogUtils.init();

    }

    /**
     * 加载isv调试插件
     */

    public static TelephonyManager getTelephonyMgr(Context context) {
        Object objTemp = context.getSystemService(Context.TELEPHONY_SERVICE);
        if (objTemp != null && objTemp instanceof TelephonyManager) {
            return (TelephonyManager) objTemp;
        } else {
            return null;
        }
    }

    /**
     * 获取deviceid。首先取设备的imei,如果为空，则取uuid。
     *
     * @param context
     * @return
     */
    @SuppressWarnings("unused")
    public static String getIMSI(Context context) {
        Object objTemp = context.getSystemService(Context.TELEPHONY_SERVICE);
        TelephonyManager phoneMgr;
        String imsi = "";
        String imei = "";
        if (objTemp != null && objTemp instanceof TelephonyManager) {
            phoneMgr = (TelephonyManager) objTemp;
            imsi = phoneMgr.getSubscriberId();
            imei = phoneMgr.getDeviceId();
        }
        return imsi != null ? imsi : "000000000000000";
    }

    /**
     * @param context 上下文项
     * @param resId   资源ID
     * @return 对应的bitmap
     */

    public static Bitmap ReadBitmap(Context context, int resId) {

        BitmapFactory.Options opt = new BitmapFactory.Options();

        opt.inPreferredConfig = Bitmap.Config.RGB_565;

        opt.inPurgeable = true;

        opt.inInputShareable = true;

        // 获取资源图片

        InputStream is = context.getResources().openRawResource(resId);

        return BitmapFactory.decodeStream(is, null, opt);

    }

    /**
     * 隐藏输入法（根据activity当前焦点所在控件的WindowToken）
     */
    public static void hideSoftInput(Activity activity, View editText) {
        View view;
        if (editText == null) {
            view = activity.getCurrentFocus();
        } else {
            view = editText;
        }

        if (view != null) {
            InputMethodManager inputMethod = (InputMethodManager) activity
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethod.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * sdcard是否可读写
     *
     * @return
     */
    public static boolean isSdcardReady() {
        return Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState());
    }

    /**
     * 网络是否已连接
     *
     * @param context
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        NetworkInfo networkInfo = ((ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE))
                .getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    /**
     * @param @return 设定文件
     * @return Long 返回类型
     * @throws
     * @Title: getCurrentSystemTime
     * @Description: 获取当前系统时间
     */
    public static Long getCurrentSystemTime() {
        return Calendar.getInstance().getTimeInMillis();
    }

    /**
     * 取出当前的日期格式yyyyMMdd HH:mm:ss
     *
     * @return
     */
    public static String getCurrentTimeStr() {
        Calendar curCalendar = Calendar.getInstance();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(curCalendar.getTime());
    }

    /**
     * 给一个字符串的毫秒值，转换为时间 yyyy-MM-dd
     *
     * @param time
     * @return
     */
    public static String string2Time(String time) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return df.format(Long.parseLong(time));
    }

    /**
     * 计算两个日期之间相差的天数
     *
     * @param smdate 较小的时间
     * @param bdate  较大的时间
     * @return 相差天数
     * @throws ParseException
     */
    public static int daysBetween(Date smdate, Date bdate)
            throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        smdate = sdf.parse(sdf.format(smdate));
        bdate = sdf.parse(sdf.format(bdate));
        Calendar cal = Calendar.getInstance();
        cal.setTime(smdate);
        long time1 = cal.getTimeInMillis();
        cal.setTime(bdate);
        long time2 = cal.getTimeInMillis();
        long between_days = (time2 - time1) / (1000 * 3600 * 24);
        int diffDays = Integer.parseInt(String.valueOf(between_days));

        return Math.abs(diffDays);
    }

    /**
     * 字符串的日期格式的计算
     */
    public static int daysBetween(String smdate, String bdate)
            throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.setTime(sdf.parse(smdate.toString()));
        long time1 = cal.getTimeInMillis();
        cal.setTime(sdf.parse(bdate.toString()));
        long time2 = cal.getTimeInMillis();
        long between_days = (time2 - time1) / (1000 * 3600 * 24);

        return Integer.parseInt(String.valueOf(between_days));
    }

    /**
     * dip2px
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * px2dip
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 获取listview 的item视图
     *
     * @param pos
     * @param listView
     * @return
     */
    public static View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition
                + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

    /**
     * 显示更新APP弹窗
     *
     * @param message
     * @param url
     */
    public static void showUpdateDialog(final Context context, String message,
                                        final String url, final boolean isForce) {
        MyDialog.Builder builder = new MyDialog.Builder(context);
        builder.setCancelable(!isForce);
        builder.setMessage(message);
        builder.setTitle("版本更新");
        builder.setConfirmButton("现在升级", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(App.getInstance(),
                        AppUpgradeService.class);
                intent.putExtra("downloadUrl", url);
                String apkFileName = url.substring(url.lastIndexOf("/") + 1);
                intent.putExtra("apkFileName", apkFileName);
                App.getInstance().startService(intent);
                dialog.dismiss();
                if (isForce) {
                    ActivityMgr.finishAllActivity(false);
                    // 退出程序
                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(1);
                }
            }
        });

        builder.setCancelButton("稍后升级",
                new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (isForce) {
                            ActivityMgr.finishAllActivity(false);
                            // 退出程序
                            android.os.Process.killProcess(android.os.Process
                                    .myPid());
                            System.exit(1);
                        }
                    }
                });
        builder.create().show();
    }

    /**
     * 初始化
     *
     * @param context
     */
    public static Map<String, String> getVersion(Context context) {
        Map<String, String> versionInfo = new HashMap<String, String>();
        PackageManager manager = context.getPackageManager();
        PackageInfo info;
        ApplicationInfo appInfo;
        try {
            info = manager.getPackageInfo(context.getPackageName(), 0);
            appInfo = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);
            versionInfo.put(VERSION_NAME, info.versionName);
            versionInfo.put(VERSION_CODE, info.versionCode + "");
        } catch (NameNotFoundException e) {
            MyLog.e("getVersion", e.toString());
        }
        return versionInfo;
    }

    public static String toUnicode(String zhStr) {
        StringBuffer unicode = new StringBuffer();
        for (int i = 0; i < zhStr.length(); i++) {
            char c = zhStr.charAt(i);
            unicode.append("\\u" + Integer.toHexString(c));
        }
        return unicode.toString();
    }

    public static long lastClickTime;

    /**
     * 防止按钮连续点击
     */
    public static boolean isFastClick() {
        long time = System.currentTimeMillis();
        if (time - lastClickTime < 500) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    /**
     * 取出当前的日期格式yyyyMMdd
     *
     * @return
     */
    @SuppressWarnings("unused")
    public static String getTimeFormatStr2(long time) {
        Calendar curCalendar = Calendar.getInstance();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(time);
    }

    /**
     * 取出当前的日期格式yyyyMMdd
     *
     * @return
     */
    @SuppressWarnings("unused")
    public static String getTimeFormatStr3(long time) {
        Calendar curCalendar = Calendar.getInstance();
        DateFormat df = new SimpleDateFormat("yyyy.MM.dd");
        return df.format(time);
    }

    /**
     * 取出当前的日期格式HH
     *
     * @return
     */
    @SuppressWarnings("unused")
    public static String getCurrentTimeHHmmStr(long time) {
        Calendar curCalendar = Calendar.getInstance();
        DateFormat df = new SimpleDateFormat("HH");
        return df.format(time);
    }

    /***
     * @param @return
     * @return boolean
     * @throws
     * @Title: loginState
     * @Description: 登陆状态获取
     */
    public static boolean loginState() {
        return SPUtils.getBoolean(ActivityMgr.mContext, GlobalInfo.KEY_ISLOGIN,
                false);
    }

    /***
     * @param @param v
     * @return void
     * @throws
     * @Title: hidiInputMethodManager
     * @Description: 隐藏软键盘
     */
    public static void hidiInputMethodManager(View v) {
        InputMethodManager imm = (InputMethodManager) v.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
        }
    }

    /***
     * @param @param  v
     * @param @return
     * @return boolean
     * @throws
     * @Title: isIMMShow
     * @Description: 判断键盘是否显示
     */
    public static boolean isIMMShow(View v) {
        InputMethodManager imm = (InputMethodManager) v.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        return imm.isActive();
    }

    /**
     * 获取当前的系统时间转换为字符串
     *
     * @return
     */
    public static String getCurrentTime() {
        long currentTimeMillis = System.currentTimeMillis();

        return currentTimeMillis + "";
    }

    /**
     * 将InputStream转换成byte数组
     *
     * @param in InputStream
     * @return byte[]
     * @throws IOException
     */
    public static byte[] InputStreamTOByte(InputStream in) throws IOException {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int count = -1;
        while ((count = in.read(data, 0, 1024)) != -1)
            outStream.write(data, 0, count);
        data = null;
        return outStream.toByteArray();
    }

    /**
     * 将String转换成InputStream
     *
     * @param in
     * @return
     * @throws Exception
     */
    public static InputStream StringTOInputStream(String in) throws Exception {
        ByteArrayInputStream is = new ByteArrayInputStream(in.getBytes("utf-8"));
        return is;
    }

    /***
     * @param @return 设定文件
     * @return PrepayIdInfo 返回类型
     * @throws
     * @Title: getPrepayIdInfo
     * @Description: 转换json对象
     */
    public static PrepayIdInfo getPrepayIdInfo() {
        PrepayIdInfo info;
        Gson gson = new Gson();
        String prepayStr = SPUtils.getString(ActivityMgr.mContext, GlobalInfo.APP_ID, "");
        JSONObject jj;
        try {
            jj = new JSONObject(prepayStr);
            return info = gson.fromJson(jj.toString(), PrepayIdInfo.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取当前Activity
     *
     * @return
     */
    public static String getCurrentActivity() {
        String activityClassName = "";
        ActivityManager am = (ActivityManager) ActivityMgr.getContext()
                .getSystemService(Activity.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        if ("com.anniu.shandiandaojia".equals(cn.getPackageName())) {
            activityClassName = cn.getClassName();
        }
        return activityClassName;
    }

    /**
     * @param @param  string
     * @param @return 设定文件
     * @return int 返回类型
     * @throws
     * @Title: stringToInt
     * @Description: String to int
     */
    public static int stringToInt(String string) {
        String str = string.substring(0, string.indexOf("."));
        String str2 = string.substring(string.indexOf(".") + 1);
        int length = str2.length();
        if (length > 1) {
            str += str2;
        } else {
            str += str2 + "0";
        }
        int intgeo = Integer.parseInt(str);
        return intgeo;
    }

    /***
     * @param @param v1
     * @param @param v2
     * @Description: 提供精确的减法运算。
     */
    public static double sub(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.subtract(b2).doubleValue();
    }

    /**
     * @param @param v1
     * @param @param v2
     * @Description: 提供精确的加法运算。
     */
    public static double add(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.add(b2).doubleValue();
    }

}

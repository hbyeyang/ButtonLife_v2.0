package com.anniu.shandiandaojia.task;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.anniu.shandiandaojia.R;
import com.anniu.shandiandaojia.app.App;
import com.anniu.shandiandaojia.utils.DownloadUtils;
import com.anniu.shandiandaojia.utils.ToastBasic;
import com.anniu.shandiandaojia.utils.Utils;

import java.io.File;

/**
 * app更新
 *
 * @author zxl
 */
public class AppUpgradeService extends Service {

    public static final int mNotificationId = 100; // 通知唯一id
    private String mDownloadUrl = null; // app更新下载地址
    private String apkFileName = "";
    private NotificationManager mNotificationManager = null; //
    private Notification mNotification = null; // 通知
    private PendingIntent mPendingIntent = null; // 延时意图

    private File destDir = null; // apk 存放目录
    private File destFile = null; // apk 文件地址

    private static final int DOWNLOAD_FAIL = -1; // 下载失败
    private static final int DOWNLOAD_SUCCESS = 0; // 下载成功
    private static final int DOWNLOAD_ING = 1;
    private Handler mHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case DOWNLOAD_SUCCESS:
                        ToastBasic.showToast(getApplicationContext(), "新版本下载成功,开始安装。", Toast.LENGTH_LONG);
                        install(destFile);
                        break;
                    case DOWNLOAD_FAIL:
                        ToastBasic.showToast(getApplicationContext(), "新版本下载失败，请稍后再试。", Toast.LENGTH_LONG);
                        mNotificationManager.cancel(mNotificationId);
                        break;
                    case DOWNLOAD_ING:
                        mNotification.contentView.setProgressBar(R.id.app_upgrade_progressbar, 100, msg.arg1, false);
                        mNotification.contentView.setTextViewText(R.id.app_upgrade_progresstext, msg.arg1 + "%"); // 更新进度
                        mNotificationManager.notify(mNotificationId, mNotification); // 刷新通知
                        break;
                }
            }

        };
    }

    private DownloadUtils.DownloadListener downloadListener = new DownloadUtils.DownloadListener() {
        @Override
        public void downloading(int progress) {
            Message msg = mHandler.obtainMessage();
            msg.what = DOWNLOAD_ING;
            msg.arg1 = progress;
            mHandler.sendMessage(msg);
        }

        @Override
        public void downloaded() { // 下载完成
            mNotification.contentView.setViewVisibility(R.id.app_upgrade_progressblock, View.GONE);
            mNotification.defaults = Notification.DEFAULT_SOUND;
            mNotification.contentIntent = mPendingIntent;
            mNotification.contentView.setTextViewText(R.id.app_upgrade_progresstext, "下载完成");
            mNotificationManager.notify(mNotificationId, mNotification);
            if (destFile.exists() && destFile.isFile() && checkApkFile(destFile.getPath())) { // 文件存在并apk包文件可以安装
                Message msg = mHandler.obtainMessage();
                msg.what = DOWNLOAD_SUCCESS;
                mHandler.sendMessage(msg);
            }
            mNotificationManager.cancel(mNotificationId);
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        mDownloadUrl = intent.getStringExtra("downloadUrl");
        apkFileName = intent.getStringExtra("apkFileName");
        if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            destDir = new File(Environment.getExternalStorageDirectory().getPath() + "/");
            if (destDir.exists()) {
                File destFile = new File(destDir.getPath() + "/" + apkFileName);
                showNotification();
            }
        } else {
            super.onStart(intent, startId);
        }

        super.onStart(intent, startId);

    }

    /**
     * 功能描述：显示更新通知 创 建 人: DavikChen 日 期： 2012-12-20 上午11:56:13 修 改 人: 日 期:
     */
    private void showNotification() {
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotification = new Notification();

        mNotification.contentView = new RemoteViews(getApplication().getPackageName(), R.layout.upgrade_notification);

        Intent completingIntent = new Intent();
        completingIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        completingIntent.setClass(getApplication().getApplicationContext(), AppUpgradeService.class);

        mPendingIntent = PendingIntent.getActivity(AppUpgradeService.this, R.string.app_name, completingIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        mNotification.icon = R.drawable.ic_launcher;
        mNotification.tickerText = "下载中";
        mNotification.contentIntent = mPendingIntent;
        mNotification.contentView.setProgressBar(R.id.app_upgrade_progressbar, 100, 0, false);
        mNotification.contentView.setTextViewText(R.id.app_upgrade_progresstext, "0%");
        mNotificationManager.cancel(mNotificationId);
        mNotificationManager.notify(mNotificationId, mNotification);
        new AppUpgradeThread().start();
    }

    class AppUpgradeThread extends Thread {

        @Override
        public void run() {
            if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
                if (destDir == null) {
                    destDir = new File(Environment.getExternalStorageDirectory().getPath() + "/");
                }
                if (destDir.exists() || destDir.mkdirs()) {
                    destFile = new File(destDir.getPath() + "/" + apkFileName);
                    try { // 下载app
                        DownloadUtils.download(mDownloadUrl, destFile, false, downloadListener);
                    } catch (Exception e) {
                        Message msg = mHandler.obtainMessage();
                        msg.what = DOWNLOAD_FAIL;
                        mHandler.sendMessage(msg);
                        e.printStackTrace();
                    }
                }
            }
            stopSelf();
        }
    }

    /**
     * 检查apk文件完整性
     *
     * @param apkFilePath apk存放路径
     * @return true:完整 false:不完整
     */
    public boolean checkApkFile(String apkFilePath) {
        boolean result = false;
        try {
            PackageManager pManager = getPackageManager();
            PackageInfo pInfo = pManager.getPackageArchiveInfo(apkFilePath, PackageManager.GET_ACTIVITIES);
            if (pInfo == null) {
                result = false;
            } else {
                int apkScard = pInfo.versionCode;
                if (apkScard >= Integer.parseInt(Utils.getVersion(App.getInstance()).get(Utils.VERSION_CODE))) {
                    result = true;
                } else {
                    result = false;
                }
            }
        } catch (Exception e) {
            result = false;
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 跳转到app安装界面
     *
     * @param apkFile apk文件存放地址
     */
    public void install(File apkFile) {
        Uri uri = Uri.fromFile(apkFile);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        startActivity(intent);
    }
}

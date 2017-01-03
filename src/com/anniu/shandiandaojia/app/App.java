package com.anniu.shandiandaojia.app;

import android.app.Application;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.widget.Toast;

import com.anniu.shandiandaojia.R;
import com.anniu.shandiandaojia.base.ActivityMgr;
import com.anniu.shandiandaojia.base.BaseActivity;
import com.anniu.shandiandaojia.base.BaseLogic;
import com.anniu.shandiandaojia.base.event.MultiHashMap;
import com.anniu.shandiandaojia.logic.Event;
import com.anniu.shandiandaojia.logic.LocationLogic;
import com.anniu.shandiandaojia.logic.UpdateLogic;
import com.anniu.shandiandaojia.service.HttpService;
import com.anniu.shandiandaojia.utils.GlobalInfo;
import com.anniu.shandiandaojia.utils.MyLog;
import com.anniu.shandiandaojia.utils.MyToast;
import com.anniu.shandiandaojia.utils.SPUtils;
import com.anniu.shandiandaojia.utils.ToastBasic;
import com.anniu.shandiandaojia.utils.Utils;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.GeofenceClient;
import com.baidu.location.LocationClient;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.DbUtils.DbUpgradeListener;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;

public class App extends Application {
    protected String TAG = "App";
    private static final MultiHashMap<Integer, BaseActivity> observers = new MultiHashMap<Integer, BaseActivity>(12);
    private Handler mUIHandler = new Handler();
    private CommonReceiver mReceiver;
    private IntentFilter mFilter;
    private ComponentName mNameYPKService;
    private static App mApp;

    public static ImageLoader mImageLoader = ImageLoader.getInstance();
    private ImageLoaderConfiguration imageLoaderConfig = null;
    private DbUtils mDbUtils = null;

    public LocationClient mLocationClient;
    public GeofenceClient mGeofenceClient;
    public MyLocationListener mMyLocationListener;
    public Vibrator mVibrator;
    public static int windowHeight;
    public static int windowWidth;
    boolean isJpushInit = false;
    boolean isUmengInit = false;
    public static IWXAPI msgApi = null;

    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;
        ActivityMgr.setContext(this);
        Utils.init(this);
        initJpush();
        initLocation();
        initHttpUtils();
        initImageLoader();
//        initImgLoader();
//		initDb();
        //注册App异常崩溃处理器
        Thread.setDefaultUncaughtExceptionHandler(AppException.getAppExceptionHandler());
        //如果同时使用了其他错误统计，如果友盟先注册，其他错误统计后注册，会导致友盟的错误统计被覆盖而无数据。（Android）
        initUmeng();
        msgApi = WXAPIFactory.createWXAPI(mApp, null);
    }

    public static App getInstance() {
        return mApp;
    }

    private void initHttpUtils() {
        mNameYPKService = new ComponentName(this, HttpService.class);
        mReceiver = new CommonReceiver();
        mFilter = new IntentFilter(HttpService.ACTION_UPDATE_UI);
        registerReceiver(mReceiver, mFilter);
    }

    private void initLocation() {
        mLocationClient = new LocationClient(this.getApplicationContext());
        mMyLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mMyLocationListener);
        mGeofenceClient = new GeofenceClient(getApplicationContext());
        mVibrator = (Vibrator) getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);
    }

    private void initUmeng() {
        if (!isUmengInit) {
            // 关闭友盟的自动统计，改为自定义统计
            MobclickAgent.setDebugMode(true);
            MobclickAgent.openActivityDurationTrack(true);
            isUmengInit = true;
        }
    }

    private void initJpush() {
        if (!isJpushInit) {
            // 初始化Jpush SDK
            JPushInterface.setDebugMode(true);
            JPushInterface.init(this);

            Set<String> tags = new HashSet<String>();
            tags.add(Utils.getVersion(this).get(Utils.VERSION_NAME).replace(".", "_"));
            if (Utils.loginState()) {
                String uid = SPUtils.getInt(this, GlobalInfo.KEY_USERCODE, 0) + "";
                if (!"".equals(uid)) {
                    JPushInterface.setAliasAndTags(ActivityMgr.getContext(), uid, tags);
                } else {
                    JPushInterface.setAliasAndTags(this, Utils.getIMSI(this), tags);
                }
            } else {
                JPushInterface.setAliasAndTags(this, Utils.getIMSI(this), tags);
            }
            isJpushInit = true;
        }
    }

    /***
     * 初始化Db
     */
    private void initDb() {
        if (mDbUtils == null) {
            mDbUtils = DbUtils.create(this, "shandiandaojia", 1, new UpgradeListener());
        }
    }

    /***
     * 获取Db实例
     **/
    public DbUtils getDbUtils() {
        if (mDbUtils == null) {
            initDb();
        }
        return mDbUtils;
    }

    public static ImageLoader initImageLoader() {
        App.getInstance().initImgLoader();
        mImageLoader.clearMemoryCache();
        return mImageLoader;
    }

    /***
     * 初始化ImageLoader
     */
    public void initImgLoader() {
        if (mImageLoader == null) {
            mImageLoader = ImageLoader.getInstance();
        }
        if (imageLoaderConfig == null || !mImageLoader.isInited()) {
            File cacheDir = StorageUtils.getOwnCacheDirectory(getApplicationContext(), "buttonLife/imageCache");
            imageLoaderConfig = new ImageLoaderConfiguration.Builder(
                    getApplicationContext())
                    .denyCacheImageMultipleSizesInMemory()// 缓存显示不同大小的同一张图片
                    .memoryCacheExtraOptions(640, 800)// 设定内存图片缓存大小限制，不设置默认为屏幕的宽高
                    .memoryCache(new UsingFreqLimitedMemoryCache(10 * 1024 * 1024))// 设定内存缓存为弱缓存
                    .discCacheSize(50 * 1024 * 1024)// 缓存到文件的最大数据
                    .discCacheFileCount(1000)// 文件数量
                    .discCache(new UnlimitedDiskCache(cacheDir))
                    .discCacheFileNameGenerator(new Md5FileNameGenerator())
                    .threadPoolSize(5).threadPriority(Thread.NORM_PRIORITY - 1)
                    .tasksProcessingOrder(QueueProcessingType.LIFO).build();
            mImageLoader.init(imageLoaderConfig);// 初始化ImageLoader的与配置。
        }
    }

    /***
     * 获得屏幕的高和宽
     */
    public void initDisplay() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        windowHeight = dm.heightPixels;
        windowWidth = dm.widthPixels;
    }

    /***
     * 发送请求
     */
    public void sendAction(Intent intent) {
        if (intent != null) {
            intent.setComponent(mNameYPKService);
            startService(intent);
        }
    }

    /***
     * 添加监听
     */
    public void addListener(BaseActivity listener, int... eventIds) {
        for (int eventId : eventIds) {
            observers.put(eventId, listener);
        }
    }

    /***
     * 移除监听
     */
    public void removeListener(BaseActivity listener) {
        observers.removeValue(listener);
    }

    public void notify(int eventId, Bundle bundle) {
        synchronized (this) {
            ArrayList<BaseActivity> ls = observers.get(eventId);
            if (ls != null) {
                BaseActivity[] arrays = new BaseActivity[ls.size()];
                ls.toArray(arrays);
                for (BaseActivity observer : arrays) {
                    if (observer != null) {
                        try {
                            observer.onNotify(eventId, bundle);
                        } catch (Exception e) {
                            MyLog.e(this.getClass().getSimpleName(), e.toString());
                        }
                    }
                }
            }
        }
    }

    /***
     * 接收网络请求回来的数据
     */
    private class CommonReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            final int eventId = intent.getIntExtra(HttpService.EXTRA_EVENT_ID, -1);
            final Bundle bundle = intent.getExtras();
            if (eventId == Event.UPDATE_SUCCESS) {
                mUIHandler.post(new Runnable() {
                    public void run() {
                        try {
                            onUpdateUIEvent(bundle);
                        } catch (Exception e) {
                            MyLog.e(TAG, "CommonReceiver mUIHandler e:" + e.toString());
                        }
                    }
                });
            } else if (eventId == Event.UPDATE_FAILED) {
                String notice = bundle.getString(BaseLogic.EXTRA_ERROR);
                MyToast.show(ActivityMgr.mContext, notice);
            } else {
                App.this.notify(eventId, bundle);
            }
        }
    }

    /***
     * app更新
     */
    public void onUpdateUIEvent(Bundle bundle) {
        String versionName = bundle.getString(UpdateLogic.EXTRA_VERSION_NAME);
        String url = bundle.getString(UpdateLogic.EXTRA_VERSION_URL);
        String description = bundle.getString(UpdateLogic.EXTRA_DISCRIPT);
        boolean forced = bundle.getBoolean(UpdateLogic.EXTRA_FORCED, false);
        int mode = bundle.getInt(UpdateLogic.EXTRA_CONTOL_TYPE);

        String oldVersion = Utils.version_name;
        boolean isNew = !oldVersion.equals(versionName);
        if (isNew) {
            Utils.showUpdateDialog(ActivityMgr.mCurActivity, description, url, forced);
        } else {
            if (mode == UpdateLogic.UPDATE_STRATEGY_USER) {
                ToastBasic.showToast(this, R.string.click_no_update, Toast.LENGTH_SHORT);
            }
        }
    }

    /***
     * 数据库升级
     */
    class UpgradeListener implements DbUpgradeListener {

        @Override
        public void onUpgrade(DbUtils db, int oldVersion, int newVersion) {
            if (oldVersion == 1) {

            }
        }
    }


    /***
     * 实现实位回调监听
     */
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            double lat = location.getLatitude();
            double lon = location.getLongitude();

            SPUtils.saveString(ActivityMgr.getContext(), GlobalInfo.LNG, lon + "");
            SPUtils.saveString(ActivityMgr.getContext(), GlobalInfo.LAT, lat + "");

            Intent intent = new Intent(LocationLogic.ACTION_GET_LOCATION);
            intent.putExtra(LocationLogic.EXTRA_LNG, lon + "");
            intent.putExtra(LocationLogic.EXTRA_LAT, lat + "");
            intent.putExtra(LocationLogic.EXTRA_PAGENUM, 1);
            intent.putExtra(LocationLogic.EXTRA_PAGESIZE, 20);
            sendAction(intent);
        }
    }
}

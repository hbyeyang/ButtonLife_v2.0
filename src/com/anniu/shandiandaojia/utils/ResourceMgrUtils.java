package com.anniu.shandiandaojia.utils;

import android.app.Activity;
import android.content.Context;
import android.content.ServiceConnection;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 
 * @ClassName: ResourceMgrUtils
 * @Description: 资源管理基类
 * @author zxl
 * @date 2014年10月11日 下午2:14:53
 * 
 */
public class ResourceMgrUtils {
	private static String TAG = "ResourceManagerUtils";

	private static Context context;

	private static Activity curActivity;

	private static ServiceConnection m_localConnection;

	private static Map<Integer, Activity> mCacheActivities = new LinkedHashMap<Integer, Activity>();

	public void init(Context context) {
		this.context = context;
	}

	private ResourceMgrUtils() {
	}

	public static void setAppContext(Context obj) {
		context = obj;
	}

	public static Context getAppContext() {
		if (context == null) {
			context = curActivity.getApplicationContext();
		}
		return context;
	}

	public static Activity getActivity() {
		return curActivity;
	}

	public static void resumeActivity(Activity activity) {
		curActivity = activity;
		if (mCacheActivities == null) {
			mCacheActivities = new LinkedHashMap<Integer, Activity>();
		}
		int hashCode = activity.hashCode();
		if (mCacheActivities.containsKey(hashCode)) {
			mCacheActivities.remove(hashCode);
		}
		mCacheActivities.put(hashCode, activity);
		MyLog.e(TAG, "resumeActivity.activity = "
				+ activity.getClass().getSimpleName()
				+ ", mCacheActivities.size() = " + mCacheActivities.size());
	}

	public static void destroyActivity(Activity activity) {
		if (mCacheActivities != null) {
			mCacheActivities.remove(activity.hashCode());

			MyLog.e(TAG, "destroyActivity.activity = "
					+ activity.getClass().getSimpleName()
					+ ", mCacheActivities.size() = " + mCacheActivities.size());
		}
	}

	public static int finishAllActivity(boolean isIgnoreCurrentActivity) {
		int finishCount = 0;
		MyLog.e(TAG, "finishAllActivity.mCacheActivities.size() = "
				+ (mCacheActivities == null ? 0 : mCacheActivities.size()));
		if (mCacheActivities != null && !mCacheActivities.isEmpty()) {
			List<Activity> activitys = new ArrayList<Activity>();
			for (Entry<Integer, Activity> entry : mCacheActivities.entrySet()) {
				activitys.add(entry.getValue());
			}
			for (Activity activity : activitys) {
				mCacheActivities.remove(activity.hashCode());
				if (!isIgnoreCurrentActivity
						|| (isIgnoreCurrentActivity && activity != curActivity)) {
					if (!activity.isFinishing()) {
						activity.finish();
						finishCount++;
						MyLog.e(TAG, "finishAllActivity.activity = "
								+ activity.getClass().getSimpleName()
								+ " finished");
					}
				}
			}
		}
		MyLog.e(TAG, "finishAllActivity.finishCount = " + finishCount);
		return finishCount;
	}

	public static ServiceConnection getLocalServiceConnection() {
		return m_localConnection;
	}

	public static void setLocalServiceConnection(
			ServiceConnection m_localConnection) {
		ResourceMgrUtils.m_localConnection = m_localConnection;
	}

}

package com.anniu.shandiandaojia.base;

import android.content.Context;
import android.support.v4.app.FragmentActivity;

import com.anniu.shandiandaojia.utils.MyLog;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

public class ActivityMgr {
	private static String TAG = "ActivityMgr";
	public static Context mContext;
	public static Context mCurActivity;
	private static LinkedHashMap<Integer, FragmentActivity> mCacheActivities;

	public static Context getContext() {
		if (mContext == null) {
			mCurActivity.getApplicationContext();
		}
		return mContext;
	}

	public static void setContext(Context context) {
		mContext = context;
	}

	public static void addActivity(FragmentActivity activity) {
		mCurActivity = activity;
		if (mCacheActivities == null) {
			mCacheActivities = new LinkedHashMap<Integer, FragmentActivity>();
		}
		int hashCode = activity.hashCode();
		if (mCacheActivities.containsKey(hashCode)) {
			mCacheActivities.remove(hashCode);
		}
		mCacheActivities.put(hashCode, activity);
		MyLog.i(TAG, "addActivity.activity = "
				+ activity.getClass().getSimpleName()
				+ ", mCacheActivities.size() = " + mCacheActivities.size());
	}

	public static void destroyActivity(FragmentActivity activity) {
		if (mCacheActivities != null) {
			mCacheActivities.remove(activity.hashCode());

			MyLog.i(TAG, "destroyActivity.activity = "
					+ activity.getClass().getSimpleName()
					+ ", mCacheActivities.size() = " + mCacheActivities.size());
		}
	}

	public static int finishAllActivity(boolean isIgnoreCurrentActivity) {
		int finishCount = 0;
		MyLog.i(TAG, "finishAllActivity.mCacheActivities.size() = "
				+ (mCacheActivities == null ? 0 : mCacheActivities.size()));
		if (mCacheActivities != null && !mCacheActivities.isEmpty()) {
			List<FragmentActivity> activitys = new ArrayList<FragmentActivity>();
			for (Entry<Integer, FragmentActivity> entry : mCacheActivities.entrySet()) {
				activitys.add(entry.getValue());
			}
			for (FragmentActivity activity : activitys) {
				mCacheActivities.remove(activity.hashCode());
				if (!isIgnoreCurrentActivity
						|| (isIgnoreCurrentActivity && activity != mCurActivity)) {
					if (!activity.isFinishing()) {
						activity.finish();
						finishCount++;
						MyLog.i(TAG, "finishAllActivity.activity = "
								+ activity.getClass().getSimpleName()
								+ " finished");
					}
				}
			}
		}
		MyLog.i(TAG, "finishAllActivity.finishCount = " + finishCount);
		return finishCount;
	}
}

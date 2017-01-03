package com.anniu.shandiandaojia.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @ClassName: SPUtils
 * @Description: SPUtils 这个类主要是用来保存或者获取数据的时候使用
 * @author zxl xl_zh@foxmail.com
 * @date 2015年5月6日 上午9:42:09
 */
public class SPUtils {
	public static final String SP_NAME = "config";
	private static SharedPreferences sp;

	public static void saveBoolean(Context ct, String key, boolean value) {
		if (sp == null)
			sp = ct.getSharedPreferences(SP_NAME, 0);
		sp.edit().putBoolean(key, value).commit();
	}

	public static boolean getBoolean(Context ct, String key, boolean defValue) {
		if (sp == null)
			sp = ct.getSharedPreferences(SP_NAME, 0);

		return sp.getBoolean(key, defValue);

	}

	public static void saveString(Context ct, String key, String value) {
		if (sp == null)
			sp = ct.getSharedPreferences(SP_NAME, 0);
		sp.edit().putString(key, value).commit();
	}

	public static String getString(Context ct, String key, String defValue) {
		if (sp == null)
			sp = ct.getSharedPreferences(SP_NAME, 0);

		return sp.getString(key, defValue);
	}

	public static void saveInt(Context ct, String key, int value) {
		if (sp == null)
			sp = ct.getSharedPreferences(SP_NAME, 0);

		sp.edit().putInt(key, value).commit();
	}

	public static int getInt(Context ct, String key, int defValue) {
		if (sp == null)
			sp = ct.getSharedPreferences(SP_NAME, 0);

		return sp.getInt(key, defValue);
	}

	public static Long getLong(Context ct, String key, long defValue) {
		if (sp == null)
			sp = ct.getSharedPreferences(SP_NAME, 0);

		return sp.getLong(key, defValue);
	}

	public static void saveLong(Context ct, String key, long defValue) {
		if (sp == null)
			sp = ct.getSharedPreferences(SP_NAME, 0);

		sp.edit().putLong(key, defValue).commit();
	}
	
	/**清除数据*/
	public static void clearData() {
		sp.edit().clear().commit();
	}
}

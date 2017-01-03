package com.anniu.shandiandaojia.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.telephony.TelephonyManager;

/**
 * @ClassName: NetUtils
 * @Description: 网络工具类
 * @author zxl
 * @date 2014年10月16日 下午4:18:11
 */
public class NetUtils {
	private static String TAG = "Net";

	/** wifi连接或读取超时单位毫秒 */
	private static final int CONNECTION_WIFI_TIMEOUT = 10000;// 10秒超时
	/** net连接或读取超时单位毫秒 */
	private static final int CONNECTION_NET_TIMEOUT = 15000;// 15秒超时
	/** wap连接或读取超时单位毫秒 */
	private static final int CONNECTION_WAP_TIMEOUT = 20000;// 20秒超时

	public static final String NETWORK_TYPE_2G = "2g";// 提交到服务器的网络类型：2g
	public static final String NETWORK_TYPE_3G = "3g";// 提交到服务器的网络类型：3g
	public static final String NETWORK_TYPE_WIFI = "wifi";// 提交到服务器的网络类型：wifi
	public static final String NETWORK_TYPE_NO = "no_net";// 提交到服务器的网络类型：无网

	public static final int SUMMARY_TYPE_WIFI = 1;// WIFI
	public static final int SUMMARY_TYPE_MOBILE = 2;// MOBILE
	public static final int SUMMARY_TYPE_OTHER = 0;// 其它

	public static boolean CheckNetwork(Context context) {
		boolean flag = false;

		try {

			ConnectivityManager cwjManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (cwjManager.getActiveNetworkInfo() != null)
				flag = cwjManager.getActiveNetworkInfo().isAvailable();

		} catch (Exception e) {
			MyLog.i("NetUtils", e.toString());
		}

		return flag;
	}

	/**
	 * 检测网络
	 */
	public static int checkNetwork(Context context) {
		// 获取当前网络
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo netInfo = cm.getActiveNetworkInfo();

		int systemConnection = GlobalInfo.OPTION_NET_NO;

		// 没有可用网络
		if (netInfo == null || !netInfo.isAvailable()) {
			// isCheckNetwork = false;
			systemConnection = GlobalInfo.OPTION_NET_NO;
			return systemConnection;
		} else {

			String typeName = netInfo.getTypeName();
			if (typeName.equalsIgnoreCase("WIFI")) {

				systemConnection = GlobalInfo.OPTION_NET_WIFI;
				return systemConnection;
			}
			TelephonyManager phoneMgr = Utils.getTelephonyMgr(context);
			if (phoneMgr == null) {
				systemConnection = GlobalInfo.OPTION_NET_NO;
				return systemConnection;
			}
			int networkType = phoneMgr.getNetworkType();
			String extraInfo = netInfo.getExtraInfo();
			// Log.v("", "networkType = "+networkType);
			// 2G网络
			if (networkType == TelephonyManager.NETWORK_TYPE_GPRS
					|| networkType == TelephonyManager.NETWORK_TYPE_EDGE
					|| networkType == TelephonyManager.NETWORK_TYPE_CDMA) {

				// Utility.systemConnection = Utility.OPTION_NET_HTTP;

				if (null != extraInfo && !"".equals(extraInfo)) {

					if ("3gwap".equalsIgnoreCase(extraInfo)
							|| "cmwap".equalsIgnoreCase(extraInfo)
							|| "ctwap".equalsIgnoreCase(extraInfo)
							|| "uniwap".equalsIgnoreCase(extraInfo)) {

						systemConnection = GlobalInfo.OPTION_NET_CMWAP_PROXY;

					} else {

						systemConnection = GlobalInfo.OPTION_NET_HTTP;
					}
				} else {

					systemConnection = GlobalInfo.OPTION_NET_HTTP;
				}

				// 3G网络
			} else if (networkType == TelephonyManager.NETWORK_TYPE_UMTS
					|| networkType == TelephonyManager.NETWORK_TYPE_HSDPA
					|| networkType == TelephonyManager.NETWORK_TYPE_EVDO_0
					|| networkType == TelephonyManager.NETWORK_TYPE_EVDO_A) {

				systemConnection = GlobalInfo.OPTION_NET_HTTP;

				// 其它
			} else {

				systemConnection = GlobalInfo.OPTION_NET_HTTP;
			}

			return systemConnection;
		}
	}

	/**
	 * 返回网络类型，wifi/3g/2g/no_net
	 * 
	 * @param context
	 * @return
	 */
	public static String getNetworkType(Context context) {
		if (null == context) {
			return NETWORK_TYPE_NO;
		}
		ConnectivityManager connectivityManager = null;
		try {
			connectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
		} catch (Exception e) {
			MyLog.i(TAG, e.toString());
		}
		if (null == connectivityManager) {
			return NETWORK_TYPE_NO;
		}
		if (!isNetworkAvailable(connectivityManager)) {
			return NETWORK_TYPE_NO;
		}
		int sum_type = getSummaryType(connectivityManager);
		if (sum_type == SUMMARY_TYPE_WIFI) {
			return NETWORK_TYPE_WIFI;
		} else if (sum_type == SUMMARY_TYPE_MOBILE) {
			final TelephonyManager telephonyManager = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			final int type = telephonyManager.getNetworkType();
			if (TelephonyManager.NETWORK_TYPE_CDMA == type
					|| TelephonyManager.NETWORK_TYPE_GPRS == type
					|| TelephonyManager.NETWORK_TYPE_EDGE == type) {
				return NETWORK_TYPE_2G;
			} else {
				return NETWORK_TYPE_3G;
			}
		}
		return "";
	}

	/**
	 * 判断是否有网络（遍历NetworkInfo）
	 */
	private static boolean isNetworkAvailable(
			ConnectivityManager connectivityManager) {

		NetworkInfo[] networkInfos = null;
		try {
			networkInfos = connectivityManager.getAllNetworkInfo();
		} catch (Throwable e) {
			MyLog.i(TAG, e.toString());
		}
		if (networkInfos != null) {
			final int length = networkInfos.length;
			for (int i = 0; i < length; i++) {
				boolean connected = false;
				try {
					connected = networkInfos[i].getState() == NetworkInfo.State.CONNECTED;
				} catch (Throwable e) {
					MyLog.i(TAG, e.toString());
				}
				if (connected) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * 判断是“手机网络”还是“无线网络”
	 */
	private static int getSummaryType(ConnectivityManager connectivityManager) {

		int result = SUMMARY_TYPE_OTHER;

		// mobile
		State mobile = null;
		try {
			mobile = connectivityManager.getNetworkInfo(
					ConnectivityManager.TYPE_MOBILE).getState();
		} catch (Throwable e) {
			MyLog.i("NetUtils", e.toString());
		}
		// wifi
		State wifi = null;
		try {
			wifi = connectivityManager.getNetworkInfo(
					ConnectivityManager.TYPE_WIFI).getState();
		} catch (Throwable e) {
			MyLog.i("NetUtils", e.toString());
		}

		if (wifi == NetworkInfo.State.CONNECTED
				|| wifi == NetworkInfo.State.CONNECTING) {
			// wifi
			result = SUMMARY_TYPE_WIFI;
		} else if (mobile == NetworkInfo.State.CONNECTED
				|| mobile == NetworkInfo.State.CONNECTING) {
			// mobile
			result = SUMMARY_TYPE_MOBILE;
		}

		return result;
	}

	/**
	 * 根据连接类型返回超时时间
	 * 
	 * @param netType
	 * @return
	 */
	public static int getConnectionTimeout(int netType) {
		switch (netType) {
		case GlobalInfo.OPTION_NET_WIFI:
			return CONNECTION_WIFI_TIMEOUT;
		case GlobalInfo.OPTION_NET_HTTP:
			return CONNECTION_NET_TIMEOUT;
		case GlobalInfo.OPTION_NET_CMWAP_PROXY:
			return CONNECTION_WAP_TIMEOUT;

		default:
			return CONNECTION_NET_TIMEOUT;
		}
	}

}

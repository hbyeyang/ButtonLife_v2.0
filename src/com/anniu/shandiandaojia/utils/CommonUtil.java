package com.anniu.shandiandaojia.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommonUtil {

	/**
	 * 获取手机串号
	 * 
	 * @param context
	 * @return
	 */
	public static String getIMSI(Context context) {
		Object objTemp = context.getSystemService(Context.TELEPHONY_SERVICE);
		TelephonyManager phoneMgr;
		String imsi = "";
		if (objTemp != null && objTemp instanceof TelephonyManager) {
			phoneMgr = (TelephonyManager) objTemp;
			imsi = phoneMgr.getSubscriberId();
		}
		return imsi;
	}

	/**
	 * 读取位图
	 * 
	 * @param context
	 * @param resId
	 * @return
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
	 * 获取网络连接状态
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
	 * 秒数转换成时间
	 * 
	 * @param time
	 * @return
	 */
	public static String secToTime(int time) {
		String timeStr = null;
		int hour = 0;
		int minute = 0;
		int second = 0;
		if (time <= 0)
			return "00:00";
		else {
			minute = time / 60;
			if (minute < 60) {
				second = time % 60;
				timeStr = unitFormat(minute) + ":" + unitFormat(second);
			} else {
				hour = minute / 60;
				if (hour > 99)
					return "99:59:59";
				minute = minute % 60;
				second = time - hour * 3600 - minute * 60;
				timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":"
						+ unitFormat(second);
			}
		}
		return timeStr;
	}

	private static String unitFormat(int i) {
		String retStr = null;
		if (i >= 0 && i < 10)
			retStr = "0" + Integer.toString(i);
		else
			retStr = "" + i;
		return retStr;
	}

	public static boolean isInstalled(Context context, String packageName) {
		final PackageManager packageManager = context.getPackageManager();
		// 获取所有已安装程序的包信息
		List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
		for (int i = 0; i < pinfo.size(); i++) {
			if (pinfo.get(i).packageName.equalsIgnoreCase(packageName))
				return true;
		}
		return false;
	}

	public static byte[] getImage(String urlstr) {
		byte[] data = null;
		try {
			URL url = new URL(urlstr);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setReadTimeout(5000);

			InputStream input = conn.getInputStream();// 到这可以直接BitmapFactory.decodeFile也行。
														// 返回bitmap

			ByteArrayOutputStream output = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = input.read(buffer)) != -1) {
				output.write(buffer, 0, len);
			}

			input.close();
			data = output.toByteArray();
			System.out.println("下载完毕！");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}

	/**
	 * 从URL中获取参数
	 * 
	 * @param url
	 * @param name
	 * @return
	 */
	public static String getParamFormUrl(String url, String name) {
		String str = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		if (!"".equals(url)) {// 如果URL不是空字符串
			url = url.substring(url.indexOf('?') + 1);
			String paramaters[] = url.split("&");
			for (String param : paramaters) {
				String values[] = param.split("=");
				paramMap.put(values[0], values[1]);
			}
		}
		if (paramMap.containsKey(name)) {
			str = paramMap.get(name);
		}

		return str;
	}

	public static int getImageHeight(int width, int width_rate, int height_rate) {
		int height = 0;
		height = (int) (Double.parseDouble(width + "") * (height_rate / width_rate));
		return height;
	}
	
	public static int getImageHeight(int width, double rate) {
		int height = 0;
		height = (int) (Double.parseDouble(width + "") * rate);
		return height;
	}
	
	
	public static long getDiffSeconds(long lastTime){
		long curTime = Utils.getCurrentSystemTime();
		long diff = (curTime-lastTime);
		return diff;
	}
	
	/**
	 * 获取listview 的item视图
	 * @param pos
	 * @param listView
	 * @return
	 */
	public static View getViewByPosition(int pos, ListView listView) {
		final int firstListItemPosition = listView.getFirstVisiblePosition();
		final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;
		
		if (pos < firstListItemPosition || pos > lastListItemPosition ) {
		    return listView.getAdapter().getView(pos, null, listView);
		} else {
		    final int childIndex = pos - firstListItemPosition;
		    return listView.getChildAt(childIndex);
		}
	}
	
	/**
	 * 字节转成KB或MB
	 * @param bytes
	 * @return
	 */
	public static String bytes2kb(long bytes) {  
        BigDecimal filesize = new BigDecimal(bytes);  
        BigDecimal megabyte = new BigDecimal(1024 * 1024);  
        float returnValue = filesize.divide(megabyte, 2, BigDecimal.ROUND_UP)  
                .floatValue();  
        if (returnValue > 1)  
            return (returnValue + "MB");  
        BigDecimal kilobyte = new BigDecimal(1024);  
        returnValue = filesize.divide(kilobyte, 2, BigDecimal.ROUND_UP)  
                .floatValue();  
        return (returnValue + "KB");  
    }
	

	public static String getMd5(byte[] buffer) throws NoSuchAlgorithmException{
	    String s  = null;
	    char hexDigist[] = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
	    MessageDigest md = MessageDigest.getInstance("MD5");
	    md.update(buffer);
	    byte[] datas = md.digest();
	    char[] str = new char[2*16];
	    int k = 0;
	    for(int i=0;i<16;i++){
	      byte b   = datas[i];
	      str[k++] = hexDigist[b>>>4 & 0xf];
	      str[k++] = hexDigist[b & 0xf];
	    }
	    s = new String(str);
	    return s;
	  }

	
	/**
	 * 32位Md5加密
	 * @param str
	 * @return
	 */
    public static String MD5(String str)  
    {  
        MessageDigest md5 = null;  
        try  {  
            md5 = MessageDigest.getInstance("MD5"); 
        }catch(Exception e) {  
            e.printStackTrace();  
            return "";  
        }  
          
        char[] charArray = str.toCharArray();  
        byte[] byteArray = new byte[charArray.length];  
          
        for(int i = 0; i < charArray.length; i++) {  
            byteArray[i] = (byte)charArray[i];  
        }  
        byte[] md5Bytes = md5.digest(byteArray);  
          
        StringBuffer hexValue = new StringBuffer();  
        for( int i = 0; i < md5Bytes.length; i++) {  
            int val = (md5Bytes[i])&0xff;  
            if(val < 16) {  
                hexValue.append("0");  
            }  
            hexValue.append(Integer.toHexString(val));  
        }  
        return hexValue.toString();  
    }
    

	public static String getDeviceInfo(Context context) {
	    try{
	      org.json.JSONObject json = new org.json.JSONObject();
	      android.telephony.TelephonyManager tm = (android.telephony.TelephonyManager) context
	          .getSystemService(Context.TELEPHONY_SERVICE);
	
	      String device_id = tm.getDeviceId();
	
	      android.net.wifi.WifiManager wifi = (android.net.wifi.WifiManager) context.getSystemService(Context.WIFI_SERVICE);
	
	      String mac = wifi.getConnectionInfo().getMacAddress();
	      json.put("mac", mac);
	
	      if( TextUtils.isEmpty(device_id) ){
	        device_id = mac;
	      }
	
	      if( TextUtils.isEmpty(device_id) ){
	        device_id = android.provider.Settings.Secure.getString(context.getContentResolver(),android.provider.Settings.Secure.ANDROID_ID);
	      }
	
	      json.put("device_id", device_id);
	
	      return json.toString();
	    }catch(Exception e){
	      e.printStackTrace();
	    }
	  return null;
	}
	
	public static boolean is3G(Context context) {   
        ConnectivityManager cm = (ConnectivityManager) context   
                .getSystemService(Context.CONNECTIVITY_SERVICE);   
        NetworkInfo networkINfo = cm.getActiveNetworkInfo();   
        if (networkINfo != null   
                && networkINfo.getType() == ConnectivityManager.TYPE_MOBILE) {   
            return true;   
        }   
        return false;   
    }
	
	public static boolean isWifi(Context context) {   
        ConnectivityManager cm = (ConnectivityManager) context   
                .getSystemService(Context.CONNECTIVITY_SERVICE);   
        NetworkInfo networkINfo = cm.getActiveNetworkInfo();
        if (networkINfo != null && networkINfo.getType() == ConnectivityManager.TYPE_WIFI) {   
            return true;   
        }
        return false;   
    }
	
	public static String getNetType(Context context){
        String type = "unknown";
        if (CommonUtil.isWifi(context)){
            type = "wifi";
        }
        else {
            
        }
        return type;
    }
}

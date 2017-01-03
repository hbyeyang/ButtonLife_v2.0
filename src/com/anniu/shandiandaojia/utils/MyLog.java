package com.anniu.shandiandaojia.utils;

import android.util.Log;


public class MyLog  {

	private static boolean isDebug = true;
	
	public static void i(String tag,String msg){
		if(isDebug){
			Log.i(tag, msg);
		}
	}
	
	public static void w(String tag,String msg){
		if(isDebug){
			Log.w(tag, msg);
		}
	}
	public static void e(String tag,String msg){
		if(isDebug){
			Log.e(tag, msg);
		}
	}
}

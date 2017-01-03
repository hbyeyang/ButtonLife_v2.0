package com.anniu.shandiandaojia.receiver;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.anniu.shandiandaojia.R;
import com.anniu.shandiandaojia.activity.MainActivity;
import com.anniu.shandiandaojia.app.App;
import com.anniu.shandiandaojia.db.jsondb.PushMessage;
import com.anniu.shandiandaojia.utils.ExampleUtil;
import com.anniu.shandiandaojia.utils.MyLog;
import com.anniu.shandiandaojia.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;

import cn.jpush.android.api.JPushInterface;

/**
 * 自定义接收器
 * 
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class MyReceiver extends BroadcastReceiver {
	private static final String TAG = "MyReceiver";
	private static final String PUSH_MESSAGE = "1";
	public static final String MSG_HREF_TYPE = "meg_href_type";
	public static final String MSG_HREF_TARGET = "meg_href_target";
	private Context mContext;
	
	private String content;
	private String toTaskbar;
	private String hrefTarget;
	private String type;
	private String hrefType;
	private Toast toast;

	@Override
	public void onReceive(Context context, Intent intent) {
		this.mContext = context;
        Bundle bundle = intent.getExtras();
		MyLog.i(TAG, "onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));
		
        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            MyLog.i(TAG, "接收Registration Id : " + regId);
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
        	MyLog.i(TAG,"收到了自定义消息。消息内容是：" + bundle.getString(JPushInterface.EXTRA_MESSAGE));
        	MyLog.i(TAG,"收到了自定义消息。消息内容是：" + bundle.getString(JPushInterface.EXTRA_EXTRA));
        	content = bundle.getString(JPushInterface.EXTRA_MESSAGE);
        	String extra = bundle.getString(JPushInterface.EXTRA_EXTRA);
        	
        	try {
				JSONObject jsonObj = new JSONObject(extra);
				toTaskbar = String.valueOf(jsonObj.get("toTaskbar")); // 是否在通知栏显示 0否，1是
				hrefTarget = String.valueOf(jsonObj.get("hrefTarget")); // 跳转目标
				type = String.valueOf(jsonObj.get("type")); // 1消息，2公告，3推送固定用户消息（不保存）
				hrefType = String.valueOf(jsonObj.get("hrefType")); // 跳转类型
				MyLog.i(TAG,toTaskbar + hrefTarget + type + hrefType);
				if (!TextUtils.isEmpty(content) && !TextUtils.isEmpty(toTaskbar) && !TextUtils.isEmpty(hrefTarget)
						&& !TextUtils.isEmpty(type) && !TextUtils.isEmpty(hrefType)) {
					receivePushMessage();
				}
			} catch (JSONException e) {
				MyLog.i(TAG,e.getMessage());
			}
        	processCustomMessage(context, bundle);
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            MyLog.i(TAG, "[MyReceiver] 接收到推送下来的通知");
            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            MyLog.i(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            MyLog.i(TAG, "[MyReceiver] 用户点击打开了通知");
        	//打开自定义的Activity
        	Intent i = new Intent(context, MainActivity.class);
        	//i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        	i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
        	context.startActivity(i);
        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
            MyLog.i(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
            //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..
        } else if(JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
        	boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
        	Log.w(TAG, "[MyReceiver]" + intent.getAction() +" connected state change to "+connected);
        } else {
        	MyLog.i(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
        }
	}

	// 打印所有的 intent extra 数据
	private static String printBundle(Bundle bundle) {
		StringBuilder sb = new StringBuilder();
		for (String key : bundle.keySet()) {
			if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
				sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
			}else if(key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)){
				sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
			} 
			else {
				sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
			}
		}
		return sb.toString();
	}
	
	//send msg to MainActivity
	private void processCustomMessage(Context context, Bundle bundle) {
		if (MainActivity.isForeground) {
			String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
			String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
			Intent msgIntent = new Intent(MainActivity.MESSAGE_RECEIVED_ACTION);
			msgIntent.putExtra(MainActivity.KEY_MESSAGE, message);
			if (!ExampleUtil.isEmpty(extras)) {
				try {
					JSONObject extraJson = new JSONObject(extras);
					if (null != extraJson && extraJson.length() > 0) {
						msgIntent.putExtra(MainActivity.KEY_EXTRAS, extras);
					}
				} catch (JSONException e) {

				}
			}
			context.sendBroadcast(msgIntent);
		}
	}
	
	/**
	 * 收到推送消息
	 */
	private void receivePushMessage() {
		MyLog.i(TAG,"receivePushMessage");
		// 判断是否要通知栏提示
		if (Integer.parseInt(toTaskbar) == 1) {// 需要通知栏提示
			MyLog.i(TAG,"创建自定义通知栏");
			NotificationManager manager = (NotificationManager) App.getInstance().getSystemService(Context.NOTIFICATION_SERVICE);
			// 创建一个Notification
			Notification notification = new Notification();
			// 设置显示在手机最上边的状态栏的图标
			notification.icon = R.drawable.ic_launcher;
			notification.tickerText = content;
			notification.flags |= Notification.FLAG_AUTO_CANCEL;
			notification.defaults = Notification.DEFAULT_SOUND;
			// 点击状态栏的图标出现的提示信息设置
			notification.setLatestEventInfo(App.getInstance(), "您有一条新消息",content, getPendingIntent());
			manager.notify((int) ((new Date()).getTime()%10000000), notification);
		}
		if(type == PUSH_MESSAGE) {
			// 保存新消息
			PushMessage pm = new PushMessage();
			pm.setContent(content);
			pm.setHrefTarget(hrefTarget);
			pm.setHrefType(Integer.parseInt(hrefType));
			pm.setIsLook(PushMessage.IS_LOOK_NO);
			pm.setReceiveDate(new Date());
			PushMessage.saveOrUpdate(pm);
		
			String curActivity = Utils.getCurrentActivity();
			if (!(curActivity.contains("PersonalActivity") || curActivity.contains("ConfigActivity") 
				|| "".equals(curActivity) || curActivity.contains("WelcomeActivity")
				|| curActivity.contains("SplashActivity"))) {
				long num = PushMessage.getUnReadMessageCount();
				toast = Toast.makeText(mContext, "您有" + num + "条新消息",Toast.LENGTH_LONG);
				toast.setGravity(Gravity.BOTTOM, App.windowWidth - (toast.getXOffset() * 2), 0);
				toast.show();
			}
		}
	}
	
	private PendingIntent getPendingIntent() {
		Intent intent = new Intent();
		ActivityManager manager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> task_info = manager.getRunningTasks(20);
		String className = "";
		for (int i = 0; i < task_info.size(); i++) {
			if ("com.anniu.shandiandaojia".equals(task_info.get(i).topActivity.getPackageName())) {
				className = task_info.get(i).topActivity.getClassName();
				// 这里是指从后台返回到前台 前两个的是关键
				try {
					intent.setClass(mContext, Class.forName(className));
					setIntent(intent);
					break;
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
		if ("".equals(className)) {
			intent.setClass(mContext, MainActivity.class);
			intent.putExtra(MSG_HREF_TYPE, hrefType);
			intent.putExtra(MSG_HREF_TARGET, hrefTarget);
		}
		MyLog.i(TAG,"className-->" + className);
		PendingIntent pendingIntent = PendingIntent.getActivity(mContext, (int) ((new Date()).getTime()%10000000),
				intent, 0);
		return pendingIntent;
	}
	
	public Intent setIntent(Intent intent) {
		if(!TextUtils.isEmpty(hrefType)) {
//			int type = Integer.parseInt(hrefType);
//			if(type == PushMessage.HREF_TYPE_GAME) {
//				intent.setClass(context, GameDetailActivity.class);
//				intent.putExtra(GamesLogic.EXTRA_GAMEID, StringUtil.String2Integer(hrefTarget, 0));
//				intent.putExtra(GamesLogic.EXTRA_GAMENAME, "");
//			} else if(type == PushMessage.HREF_TYPE_RACE) {
//				intent.setClass(context, MatchDetailActivity.class);
//				List<String> ids= StringUtil.String2List(hrefTarget, ",");
//				intent.putExtra(RepKey.KEY_MATCHID, StringUtil.String2Integer((ids!=null&&ids.size()>0)?ids.get(0):null, 0));
//				intent.putExtra(MatchesLogic.KEY_ROOMID, (ids!=null&&ids.size()>1)?ids.get(1):"");
//			} else if(type == PushMessage.HREF_TYPE_LIVE) {
//				intent.setClass(context, LiveActivity.class);
//				intent.putExtra(LiveActivity.EXTRA_MEDIA_ID, hrefTarget);
//			} else if(type == PushMessage.HREF_TYPE_SCREEN) {
//				intent.setClass(context, VideoActivity.class);
//				intent.putExtra(VideoActivity.EXTRA_MEDIA_ID, hrefTarget);
//			} else if(type == PushMessage.HREF_TYPE_HTML) {
//				intent.setClass(context, WebViewActivity.class);
//				if(!StringUtil.isEmpty(hrefTarget) && hrefTarget.startsWith("http://")) {
//					intent.putExtra("url",hrefTarget);
//				} else {
//					intent.putExtra("url","http://" + hrefTarget);
//				}
//			} else if(type == PushMessage.HREF_TYPE_HAND_GAME) {
//				intent.setClass(context, HandGameDetailActivity.class);
//				intent.putExtra(GamesLogic.EXTRA_GAMEID, StringUtil.String2Integer(hrefTarget, 0));
//				intent.putExtra(GamesLogic.EXTRA_GAMENAME, "");
//			}
		}
		return intent;
	}
}

package com.anniu.shandiandaojia.utils;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import com.anniu.shandiandaojia.R;
import com.anniu.shandiandaojia.view.CommonDialog;

/**
* @ClassName: DialogUtils 
* @Description: 对话框工具类
* @author zxl 
* @date 2014年10月11日 下午2:45:22
 */
public class DialogUtils {

    private static Activity mActivity = null;

    public static void init() {

    }

    /**
     * handler处理
     */
    public static Handler handler = new Handler() {

        public void handleMessage(Message msg) {

            switch (msg.what) {
                case 0:
                    Toast netToast = Toast.makeText(mActivity,
                            String.valueOf(msg.obj), Toast.LENGTH_SHORT);
                    netToast.setText(String.valueOf(msg.obj));
                    // Util.toastCancel(Utility.netToast);
                    netToast.show();
                    break;
                case 1:
                    showCustomeDialog(mActivity, String.valueOf(msg.obj));
            }
            super.handleMessage(msg);
        }
    };

    /**
     * 提示信息
     * 
     * @param activity
     * @param message
     */
    public static void showInfo(Activity activity, String message) {

        if (null == activity || activity.isFinishing()) {
            return;
        }
        mActivity = activity;
        Message msg = new Message();
        msg.what = 0;
        msg.obj = message;
        handler.sendMessage(msg);
    }

    /**
     * 提示后台服务信息
     */
    public static void showServiceInfo(Activity activity, String message) {
        if (null == activity || activity.isFinishing()) {
            return;
        }
        mActivity = activity;
        Message msg = new Message();
        msg.what = 1;
        msg.obj = message;
        handler.sendMessage(msg);

    }

    public static void showCustomeDialog(Activity activity, String message) {
        showCustomeDialog(activity, message, null);
    }

    public static void showCustomeDialog(Activity activity, String message,
            DialogInterface.OnClickListener listener) {
        final CommonDialog dialog = new CommonDialog(activity);
        dialog.setSingleButton(true);
        dialog.setTitle(activity.getString(R.string.dialog_title_notice));
        dialog.setMessage(message);
        dialog.setPositiveButton("确定", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
    
    public static void showTicketDialog(Activity activity, String message,
    		DialogInterface.OnClickListener listener) {
    	final CommonDialog dialog = new CommonDialog(activity);
    	dialog.setSingleButton(true);
    	dialog.setTitle("支付成功");
    	dialog.setMessage(message);
    	dialog.setPositiveButton("知道了", new View.OnClickListener() {
    		@Override
    		public void onClick(View v) {
    			dialog.dismiss();
    		}
    	});
    }
}

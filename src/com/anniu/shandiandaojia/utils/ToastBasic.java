package com.anniu.shandiandaojia.utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

public class ToastBasic {

    public static Toast toast;

    public static void showToast(Context context, String text, int duration) {

        toast = Toast.makeText(context, text, duration);

        toast.show();
    }

    public static void showToast(Context context, int resId, int duration) {

        toast = Toast.makeText(context, resId, duration);

        toast.show();
    }

    public static void showToast(Context context, String text) {

        // TextView
        // tv=(TextView)Splash.toastRoot.findViewById(R.id.TextViewInfo);
        // tv.setText(text);
        toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        // toast = new Toast(context);
        // toast.setText(text);
        // toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }

    // 设置显示的位置
    public static void showToastLocal(String text) {
        // TextView
        // tv=(TextView)Splash.toastRoot.findViewById(R.id.TextViewInfo);
        // tv.setText(text);
        toast.setText(text);
        toast.setGravity(Gravity.BOTTOM, 0, 0);
        toast.show();
    }

    public static void showToast(int resId) {

        toast.setText(resId);

        toast.show();
    }

    public static void closeToast() {

        toast.cancel();
    }

    public static void setToastLocation(int gravity) {

        toast.setGravity(gravity, 0, 0);

    }

}

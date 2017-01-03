package com.anniu.shandiandaojia.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * @author YY
 * @ClassName: MyToast
 * @Description: 自定义的toast工具类，只展示一个toast，不重复展示
 * @date 2015年5月29日 下午6:10:29
 */
public class MyToast {
    private static Toast mToast;

    public static void show(Context mContext, String text) {

        if (mToast != null) {
            mToast.setText(text);
        } else {
            mToast = Toast.makeText(mContext, text, Toast.LENGTH_SHORT);
        }

        mToast.show();
    }
}

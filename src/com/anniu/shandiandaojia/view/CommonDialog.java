package com.anniu.shandiandaojia.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.anniu.shandiandaojia.R;


/**
* @ClassName: CommonDialog 
* @Description: 自定义对话框 
* @author zxl 
* @date 2014年10月11日 下午2:49:30
 */
public class CommonDialog {

    private Context context;
    private AlertDialog ad;
    private TextView titleView;
    private TextView messageView;
    private LinearLayout buttonLayout;
    protected Button confirmBtn;
    protected Button cancelBtn;
    private LinearLayout cancleLayout;

    public CommonDialog(Context context) {
        this.context = context;
        ad = new android.app.AlertDialog.Builder(context).create();
        ad.show();
        // 关键在下面的两行,使用window.setContentView,替换整个对话框窗口的布局
        Window window = ad.getWindow();
        Display mDisplay = ((Activity) context).getWindowManager().getDefaultDisplay();
        int W = (int) (mDisplay.getWidth() * 0.8f);
        int H = (int) (W * 0.9f);
        window.setContentView(R.layout.commondialog);
        // window.set
        window.setLayout(W, H);
        titleView = (TextView) window.findViewById(R.id.title);
        // 默认隐藏标题
        titleView.setVisibility(View.GONE);
        messageView = (TextView) window.findViewById(R.id.message);
        buttonLayout = (LinearLayout) window.findViewById(R.id.buttonLayout);
        confirmBtn = (Button) window.findViewById(R.id.confirmBtn);
        cancelBtn = (Button) window.findViewById(R.id.cancelBtn);
        cancleLayout = (LinearLayout) window.findViewById(R.id.cancleLayout);
    }

    public void setCancel(boolean cancel) {
        ad.setCancelable(cancel);
        ad.setCanceledOnTouchOutside(cancel);
    }

    /**
     * 设置一个确定按钮
     * 
     * @param b
     */
    public void setSingleButton(boolean b) {
        if (b && null != cancleLayout) {
            cancleLayout.setVisibility(View.GONE);
        }
    }

    public void setTitle(String title) {
        titleView.setVisibility(View.VISIBLE);
        titleView.setText(title);
    }

    public void setTitle(int stringId) {
        titleView.setVisibility(View.VISIBLE);
        titleView.setText(stringId);
    }

    public void setMessage(String message) {
        messageView.setText(message);
    }

    public void setMessage(int stringId) {
        messageView.setText(stringId);
    }

    /**
     * 设置按钮
     * 
     * @param text
     * @param listener
     */
    public void setPositiveButton(String text,
            final View.OnClickListener listener) {
        confirmBtn.setText(text);
        confirmBtn.setOnClickListener(listener);
        /*
         * Button button=new Button(context); LinearLayout.LayoutParams
         * params=new LayoutParams(LayoutParams.WRAP_CONTENT,
         * LayoutParams.WRAP_CONTENT); button.setLayoutParams(params);
         * //button.setBackgroundResource(R.drawable.alertdialog_button);
         * button.setText(text); //button.setTextColor(Color.WHITE);
         * button.setTextSize(16); button.setOnClickListener(listener);
         * buttonLayout.addView(button);
         */
    }

    public void setCancelBtnTag(int mode) {
        if (cancelBtn != null) {
            cancelBtn.setTag(mode);
        }
    }

    public void setConfirmBtnTag(int mode) {
        if (confirmBtn != null) {
            confirmBtn.setTag(mode);
        }
    }

    /**
     * 取消按钮
     * 
     * @param text
     * @param listener
     */
    public void setNegativeButton(String text,
            final View.OnClickListener listener) {
        cancelBtn.setText(text);
        cancelBtn.setOnClickListener(listener);
        /*
         * Button button=new Button(context); LinearLayout.LayoutParams
         * params=new LayoutParams(LayoutParams.WRAP_CONTENT,
         * LayoutParams.WRAP_CONTENT); button.setLayoutParams(params);
         * //button.setBackgroundResource(R.drawable.alertdialog_button);
         * button.setText(text); //button.setTextColor(Color.WHITE);
         * button.setTextSize(20); button.setOnClickListener(listener);
         * if(buttonLayout.getChildCount()>0) { params.setMargins(20, 0, 0, 0);
         * button.setLayoutParams(params); buttonLayout.addView(button, 1);
         * }else{ button.setLayoutParams(params); buttonLayout.addView(button);
         * }
         */

    }

    /**
     * 关闭对话框
     */
    public void dismiss() {
        ad.dismiss();
    }

    public void show() {
        ad.show();
    }
}

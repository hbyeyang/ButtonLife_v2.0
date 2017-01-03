package com.anniu.shandiandaojia.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.anniu.shandiandaojia.R;
import com.anniu.shandiandaojia.app.App;
import com.anniu.shandiandaojia.base.ActivityMgr;
import com.anniu.shandiandaojia.base.BaseActivity;
import com.anniu.shandiandaojia.base.BaseLogic;
import com.anniu.shandiandaojia.logic.Event;
import com.anniu.shandiandaojia.logic.UserLogic;
import com.anniu.shandiandaojia.net.GlobalValue;
import com.anniu.shandiandaojia.utils.GlobalInfo;
import com.anniu.shandiandaojia.utils.MyToast;
import com.anniu.shandiandaojia.utils.SPUtils;
import com.anniu.shandiandaojia.utils.Utils;

/**
 * @author YY
 * @ClassName: RegistActivity
 * @Description: 注册登录页面
 * @date 2015年6月3日 下午3:30:26
 */
public class RegistActivity extends BaseActivity {
    private TextView titleBarTv, login, tvSendVerfCode;
    private EditText phone, verificationCode;
    private int num = 60;// 倒计时60s
    private RelativeLayout loadingView;
    private Handler handler = new Handler();

    @Override
    public void onClick(View v) {
        if (Utils.isFastClick()) {
            return;
        }

        String phoneNum = phone.getText().toString().trim();
        if (v.getId() == R.id.tv_SendVerfiCode) {
            if (TextUtils.isEmpty(phoneNum)) {
                MyToast.show(this, "手机号码不能为空！");
                return;
            }
            if (phoneNum.length() != 11) {
                MyToast.show(this, "请输入正确的手机号码！");
                return;
            }

            String text = tvSendVerfCode.getText().toString();
            if (text.equals("获取验证码")) {
                loadingView.setVisibility(View.VISIBLE);
                sendVerficationAction(phoneNum);
            } else if (text.equals("重新发送")) {
                loadingView.setVisibility(View.VISIBLE);
                sendVerficationAction(phoneNum);
                num = 60;
            } else {
                MyToast.show(this, "请等待" + num + "秒后重新发送！");
                return;
            }
        } else if (v.getId() == R.id.bt_login) {
            String verficationcode = verificationCode.getText().toString();
            if (TextUtils.isEmpty(phoneNum)) {
                MyToast.show(this, "手机号码不能为空！");
                return;
            }
            if (TextUtils.isEmpty(verficationcode)) {
                MyToast.show(this, "验证码不能为空");
                return;
            }
            if (verficationcode.length() != 6) {
                MyToast.show(this, "验证码长度不符合要求");
                return;
            }

            loadingView.setVisibility(View.VISIBLE);
            sendCheckOutVerficationCodeAction(phoneNum, verficationcode);
        } else if (v.getId() == R.id.title_bar_left) {
            finish();
        }
    }

    @Override
    protected void init(Bundle saveInstanceState) {
        setContentView(R.layout.activity_regist_login);
        titleBarTv = (TextView) findViewById(R.id.title_bar_tv);
        titleBarTv.setText(R.string.login_in);

        findViewById(R.id.iv_logo).setBackgroundDrawable(getResources().getDrawable(R.drawable.arrow_back));
        findViewById(R.id.title_bar_left).setOnClickListener(this);

        phone = (EditText) findViewById(R.id.et_phone);
        verificationCode = (EditText) findViewById(R.id.et_verification_code);
        login = (TextView) findViewById(R.id.bt_login);
        login.setOnClickListener(this);

        tvSendVerfCode = (TextView) findViewById(R.id.tv_SendVerfiCode);
        tvSendVerfCode.setOnClickListener(this);
        loadingView = (RelativeLayout) findViewById(R.id.rl_loading);
    }

    @Override
    protected void onUIEvent(int eventId, Bundle bundle) {
        String notice = bundle.getString(BaseLogic.EXTRA_ERROR);
        switch (eventId) {
            case Event.POST_SEND_VERFICATION_SUCCESS://发送验证码成功事件
                loadingView.setVisibility(View.GONE);
                boolean isSuccess = bundle.getBoolean(GlobalValue.KEY_SUCCESS);
                String result = bundle.getString(BaseLogic.EXTRA_ERROR);
                if (!isSuccess) {
                    MyToast.show(this, result);
                } else {
                    MyToast.show(this, "验证码已发送至您手机，请注意查收！");
                    handler.postDelayed(runnable, 1000);
                }
                break;
            case Event.POST_SEND_VERFICATION_FAILED://发送验证码失败事件
                loadingView.setVisibility(View.GONE);
                MyToast.show(this, notice);
                break;
            case Event.POST_CHECK_OUT_VERFICATION_SUCCESS://验证码校验并登录成功事件
                loadingView.setVisibility(View.GONE);
                SPUtils.saveBoolean(ActivityMgr.mContext, GlobalInfo.KEY_ISLOGIN, true);
                SPUtils.saveBoolean(this, GlobalInfo.KEY_NEEDLOAD, true);
                MyToast.show(this, "恭喜您登录成功！");
                startActivity(new Intent(this, MainActivity.class));//登录成功后跳转到主界面，如果不退出，就不再进入登录界面
                finish();
                break;
            case Event.POST_CHECK_OUT_VERFICATION_FAILED://验证码校验并登录失败事件
                loadingView.setVisibility(View.GONE);
                MyToast.show(this, notice);
                break;

            default:
                break;
        }
    }

    @Override
    protected void addEventListener() {
        App.getInstance().addListener(this,
                Event.POST_SEND_VERFICATION_SUCCESS, Event.POST_SEND_VERFICATION_FAILED,
                Event.POST_CHECK_OUT_VERFICATION_SUCCESS, Event.POST_CHECK_OUT_VERFICATION_FAILED);
    }

    @Override
    protected void removeListener() {
        App.getInstance().removeListener(this);
    }

    /**
     * 获取验证码
     */
    private void sendVerficationAction(String phoneNum) {
        Intent intent = new Intent(UserLogic.ACTION_POST_VERFICATION_CODE);
        intent.putExtra(UserLogic.EXTRA_PHONE, phoneNum);
        sendAction(intent);
    }

    /**
     * 校验验证码并登录
     */
    private void sendCheckOutVerficationCodeAction(String phone, String verficationCode) {
        Intent intent = new Intent(UserLogic.ACTION_POST_CHECK_OUT_VERFICATION_CODE);
        intent.putExtra(UserLogic.EXTRA_PHONE, phone);
        intent.putExtra(UserLogic.EXTRA_VERTIFYCODE, verficationCode);
        sendAction(intent);
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            num--;
            tvSendVerfCode.setText(num + "s");
            handler.postDelayed(this, 1000);
            if (num < 0) {
                tvSendVerfCode.setText("重新发送");
                handler.removeCallbacks(runnable);
            }
        }
    };
}

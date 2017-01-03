package com.anniu.shandiandaojia.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.anniu.shandiandaojia.R;
import com.anniu.shandiandaojia.app.App;
import com.anniu.shandiandaojia.base.BaseActivity;
import com.anniu.shandiandaojia.utils.GlobalInfo;
import com.anniu.shandiandaojia.utils.SPUtils;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;

/**
 * @author zxl
 * @ClassName: WelcomeActivity
 * @Description: 欢迎页
 * @date 2015年5月7日 下午2:02:12
 */
public class WelcomeActivity extends BaseActivity {

    @Override
    public void onClick(View v) {
    }


    @Override
    protected void onUIEvent(int eventId, Bundle bundle) {

    }

    @Override
    protected void addEventListener() {

    }

    @Override
    protected void removeListener() {

    }

    @Override
    protected void init(Bundle saveInstanceState) {
        setContentView(R.layout.activity_splash);
        // 获取屏幕宽高
        App.getInstance().initDisplay();
        MobclickAgent.updateOnlineConfig(this);
        AnalyticsConfig.enableEncrypt(false);

        new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (SPUtils.getBoolean(WelcomeActivity.this, GlobalInfo.IS_FIRST, false)) {
                    boolean isLocation = SPUtils.getBoolean(WelcomeActivity.this, GlobalInfo.HAS_LOCATION, false);
                    if (isLocation) {
                        startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                    } else {
                        startActivity(new Intent(WelcomeActivity.this, LocationActivity.class));
                    }
                } else {
                    startActivity(new Intent(WelcomeActivity.this, GuaidActivity.class));
                    SPUtils.saveBoolean(WelcomeActivity.this, GlobalInfo.IS_FIRST, true);
                }
                finish();
                return true;
            }
        }).sendEmptyMessageDelayed(0, 3000);
    }
}

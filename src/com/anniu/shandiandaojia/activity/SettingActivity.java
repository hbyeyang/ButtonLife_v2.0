package com.anniu.shandiandaojia.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.anniu.shandiandaojia.R;
import com.anniu.shandiandaojia.app.App;
import com.anniu.shandiandaojia.base.BaseActivity;
import com.anniu.shandiandaojia.base.BaseLogic;
import com.anniu.shandiandaojia.logic.Event;
import com.anniu.shandiandaojia.logic.UpdateLogic;
import com.anniu.shandiandaojia.logic.UserLogic;
import com.anniu.shandiandaojia.net.GlobalValue;
import com.anniu.shandiandaojia.utils.GlobalInfo;
import com.anniu.shandiandaojia.utils.SPUtils;
import com.anniu.shandiandaojia.utils.ToastBasic;
import com.anniu.shandiandaojia.utils.Utils;

/**
 * @author YY
 * @ClassName: SettingActivity
 * @Description: 设置界面
 * @date 2015年5月30日 下午8:03:50
 */
public class SettingActivity extends BaseActivity {
    private TextView titleBarTv;//界面标题

    @Override
    public void onClick(View v) {
        if (Utils.isFastClick()) {
            return;
        }
        if (v.getId() == R.id.title_bar_left) {
            finish();
        } else if (v.getId() == R.id.rl_version) {//版本检查
            sendUpdateAction();
        } else if (v.getId() == R.id.rl_about_us) {//关于我们
            startActivity(new Intent(this, AboutActivity.class));
        } else if (v.getId() == R.id.logout) {//退出
            sendLoginoutAction();
        }
    }

    @Override
    protected void init(Bundle saveInstanceState) {
        setContentView(R.layout.activity_setting);

        titleBarTv = (TextView) findViewById(R.id.title_bar_tv);
        titleBarTv.setText("设置");

        findViewById(R.id.iv_logo).setBackgroundDrawable(getResources().getDrawable(R.drawable.arrow_back));
        findViewById(R.id.title_bar_left).setOnClickListener(this);

        findViewById(R.id.rl_version).setOnClickListener(this);
        findViewById(R.id.rl_about_us).setOnClickListener(this);
        findViewById(R.id.logout).setOnClickListener(this);
    }

    @Override
    protected void onUIEvent(int eventId, Bundle bundle) {
        String notice = bundle.getString(BaseLogic.EXTRA_ERROR);
        switch (eventId) {
            case Event.GET_LOGINOUT_SUCESS:
                sendLoginOut();
                break;
            case Event.GET_LOGINOUT_FAILED:
                ToastBasic.showToast(this, notice);
                break;
        }
    }

    @Override
    protected void addEventListener() {
        App.getInstance().addListener(this, Event.GET_LOGINOUT_SUCESS, Event.GET_LOGINOUT_FAILED);
    }

    @Override
    protected void removeListener() {
        App.getInstance().removeListener(this);
    }


    /**
     * 退出登录
     */
    private void sendLoginOut() {
        startActivity(new Intent(SettingActivity.this, MainActivity.class));
        SPUtils.saveString(this, GlobalInfo.ADDRESS, "");
        SPUtils.saveInt(this, GlobalInfo.KEY_USERCODE, 0);
        SPUtils.saveBoolean(this, GlobalInfo.KEY_NEEDLOAD, true);
        SPUtils.saveString(this, GlobalInfo.KEY_USER_TEL, "");
        SPUtils.saveBoolean(this, GlobalInfo.KEY_ISLOGIN, false);
        SPUtils.saveString(this, GlobalInfo.KEY_USER_NAME, "");
        SPUtils.saveString(this, GlobalInfo.KEY_USER_SEX, "");
        SPUtils.saveString(this, GlobalInfo.KEY_NICK_NAME, "");
        SPUtils.saveString(this, GlobalValue.KEY_COOKIE, "");
    }

    /**
     * 版本检查
     */
    private void sendUpdateAction() {
        Intent intent = new Intent(UpdateLogic.ACTION_GET_UPDATEINFO);
        intent.putExtra(UpdateLogic.EXTRA_UPDATE_MODE, UpdateLogic.UPDATE_STRATEGY_USER);
        sendAction(intent);
    }

    //登出
    private void sendLoginoutAction() {
        sendAction(new Intent(UserLogic.ACTION_LOGINOUT));
    }

}

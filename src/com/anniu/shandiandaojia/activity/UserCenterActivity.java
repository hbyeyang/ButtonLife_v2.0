package com.anniu.shandiandaojia.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.anniu.shandiandaojia.R;
import com.anniu.shandiandaojia.app.App;
import com.anniu.shandiandaojia.base.BaseActivity;
import com.anniu.shandiandaojia.base.BaseLogic;
import com.anniu.shandiandaojia.db.jsondb.UserInfo;
import com.anniu.shandiandaojia.logic.Event;
import com.anniu.shandiandaojia.logic.UserLogic;
import com.anniu.shandiandaojia.utils.GlobalInfo;
import com.anniu.shandiandaojia.utils.MyToast;
import com.anniu.shandiandaojia.utils.SPUtils;
import com.anniu.shandiandaojia.utils.Utils;
import com.anniu.shandiandaojia.view.MyDialog;
import com.sharesdk.onekeyshare.OnekeyShare;

import cn.sharesdk.framework.ShareSDK;

/**
 * @author zxl
 * @ClassName: UserCenterActivity
 * @Description: 个人中心
 * @date 2015年5月7日 下午2:09:10
 */
public class UserCenterActivity extends BaseActivity {
    private TextView titleBarTv, userName, userPhone;
    private UserInfo user;

    @Override
    public void onClick(View v) {
        if (Utils.isFastClick()) {
            return;
        }
        Intent intent = new Intent();
        if (v.getId() == R.id.rl_mine_order_list) {//我的订单
            intent.setClass(this, MyOrderActivity.class);
            intent.putExtra(OrderDetailsActivity.EXTRA_FROM, 0);
            startActivity(intent);
        } else if (v.getId() == R.id.rl_mine_collect) {//我的水票
            intent.setClass(this, WaterTicketActivity.class);
            intent.putExtra(WaterTicketActivity.EXTRA_FROM, 0);
            startActivity(intent);
        } else if (v.getId() == R.id.rl_my_address) {//我的地址
            intent.setClass(this, AddressListActivity.class);
            intent.putExtra(AddressListActivity.EXTRA_FROM, 0);
            startActivity(intent);
        }
//		else if (v.getId()==R.id.rl_user_news) {//消息中心
//			startActivity(new Intent(UserCenterActivity.this,MessageCenterActivity.class));
//		}
        else if (v.getId() == R.id.rl_user_feedback) {//意见反馈
            startActivity(new Intent(this, FeedBackActivity.class));
        }
//        else if (v.getId() == R.id.rl_user_richscan) {//扫一扫
//			startActivity(new Intent(this,SweepActivity.class));
//        }
        else if (v.getId() == R.id.rl_user_question) {//常见问题
            startActivity(new Intent(UserCenterActivity.this, FAQActivity.class));
        } else if (v.getId() == R.id.rl_notescontact) {//联系客服
            callPhone();
        } else if (v.getId() == R.id.rl_mine_integration) {//我的优惠券
            startActivity(new Intent(this, CouponsActivity.class));
        } else if (v.getId() == R.id.title_bar_left) {
            finish();
        } else if (v.getId() == R.id.title_bar_right) {//设置
            startActivity(new Intent(this, SettingActivity.class));
        } else if (v.getId() == R.id.rl_user_info) {//个人中心
            intent.setClass(this, UserInfoActivity.class);
            intent.putExtra(UserInfoActivity.EXTRA_USER, user);
            startActivity(intent);
        } else if (v.getId() == R.id.rl_user_share) {//分享界面
            showShare();
        }
    }

    @Override
    protected void init(Bundle saveInstanceState) {
        setContentView(R.layout.activity_user_center);
        ShareSDK.initSDK(this);
        titleBarTv = (TextView) findViewById(R.id.title_bar_tv);
        titleBarTv.setText("个人中心");

        findViewById(R.id.iv_logo).setBackgroundDrawable(getResources().getDrawable(R.drawable.arrow_back));//点击返回
        findViewById(R.id.title_bar_left).setOnClickListener(this);

        findViewById(R.id.iv_logo_right).setBackgroundDrawable(getResources().getDrawable(R.drawable.setting_set));//点击进入设置
        findViewById(R.id.title_bar_right).setOnClickListener(this);

        findViewById(R.id.rl_mine_order_list).setOnClickListener(this);//我的订单
        findViewById(R.id.rl_mine_collect).setOnClickListener(this);//我的水票
        findViewById(R.id.rl_mine_integration).setOnClickListener(this);//代金券
        findViewById(R.id.rl_my_address).setOnClickListener(this);//我的地址
//		findViewById(R.id.rl_user_news).setOnClickListener(this);//消息中心
        findViewById(R.id.rl_user_feedback).setOnClickListener(this);//意见反馈
//		findViewById(R.id.rl_user_richscan).setOnClickListener(this);//扫一扫
        findViewById(R.id.rl_user_question).setOnClickListener(this);//常见问题
        findViewById(R.id.rl_notescontact).setOnClickListener(this);//联系客服
        findViewById(R.id.rl_user_info).setOnClickListener(this);//个人信息
        findViewById(R.id.rl_user_share).setOnClickListener(this);//分享
        userName = (TextView) findViewById(R.id.tv_user_name);
        userPhone = (TextView) findViewById(R.id.tv_user_phone);

        getInitInfo();
    }

    @Override
    protected void onUIEvent(int eventId, Bundle bundle) {
        String notice = bundle.getString(BaseLogic.EXTRA_ERROR);
        switch (eventId) {
            case Event.GET_PERSON_SETTING_SUCESS://获取个人中心设置成功事件
                user = (UserInfo) bundle.getSerializable(UserLogic.EXTRA_USER);
                userName.setText(user.getNickName());
                userPhone.setText(user.getTel());
                break;
            case Event.GET_PERSON_SETTING_FAILED://获取个人中心设置失败事件
                MyToast.show(this, notice);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean needLoad = SPUtils.getBoolean(this, GlobalInfo.KEY_NEEDLOAD_USERINFO, false);
        if (needLoad) {
            getInitInfo();
            SPUtils.saveBoolean(this, GlobalInfo.KEY_NEEDLOAD_USERINFO, false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ShareSDK.stopSDK(this);
    }


    @Override
    protected void addEventListener() {
        App.getInstance().addListener(this,
                Event.GET_PERSON_SETTING_SUCESS, Event.GET_PERSON_SETTING_FAILED);
    }

    @Override
    protected void removeListener() {
        App.getInstance().removeListener(this);
    }

    /**
     * 获取个人中心详情
     */
    private void getInitInfo() {
        sendAction(new Intent(UserLogic.ACTION_GET_PERSON_SETTING));
    }

    /**
     * 拨打电话
     */
    private void callPhone() {
        MyDialog.Builder builder = new MyDialog.Builder(this);
        builder.setTitle("拨打客服");
        builder.setMessage("客服：" + getResources().getString(R.string.client_server));
        builder.setConfirmButton("拨号", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                startActivity(new Intent(Intent.ACTION_CALL,
                        Uri.parse("tel:" + getResources().getString(R.string.client_server))));
            }
        });
        builder.setCancelButton("取消", new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void showShare() {
        OnekeyShare oks = new OnekeyShare();
        oks.disableSSOWhenAuthorize();
        oks.setTitle(getString(R.string.share));
        oks.setText(getString(R.string.share_notice));
        oks.setImageUrl("http://imgsrc.baidu.com/forum/pic/item/e17ff8dcd100baa11d36071a4510b912c8fc2e3f.jpg");
        oks.setUrl("http://a.app.qq.com/o/simple.jsp?pkgname=com.anniu.shandiandaojia");
        oks.setSilent(true);
        oks.show(this);
    }
}

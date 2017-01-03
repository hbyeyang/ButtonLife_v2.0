package com.anniu.shandiandaojia.wxapi;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import com.anniu.shandiandaojia.R;
import com.anniu.shandiandaojia.activity.BalanceActivity;
import com.anniu.shandiandaojia.activity.MyOrderActivity;
import com.anniu.shandiandaojia.activity.OrderDetailsActivity;
import com.anniu.shandiandaojia.activity.WaterTicketActivity;
import com.anniu.shandiandaojia.activity.WaterTicketDetailActivity;
import com.anniu.shandiandaojia.app.App;
import com.anniu.shandiandaojia.base.BaseActivity;
import com.anniu.shandiandaojia.base.BaseLogic;
import com.anniu.shandiandaojia.db.jsondb.PayStatus;
import com.anniu.shandiandaojia.db.jsondb.Payment;
import com.anniu.shandiandaojia.logic.Event;
import com.anniu.shandiandaojia.logic.OrderLogic;
import com.anniu.shandiandaojia.utils.GlobalInfo;
import com.anniu.shandiandaojia.utils.MyLog;
import com.anniu.shandiandaojia.utils.MyToast;
import com.anniu.shandiandaojia.utils.SPUtils;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

public class WXPayEntryActivity extends BaseActivity implements
        IWXAPIEventHandler {
    private IWXAPI api;
    int from;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_result);
        api = App.msgApi;
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
    }

    @Override
    public void onResp(BaseResp resp) {
        MyLog.e(" ", "resp errCode = " + resp.errCode);
        from = SPUtils.getInt(this, GlobalInfo.KEY_FROM, 0);
        SPUtils.saveBoolean(this, GlobalInfo.KEY_NEEDLOAD, true);
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            switch (resp.errCode) {
                case 0:
                    checkPayState();
                    break;
                case -1:
                    failed();
                    break;
                case -2:
                    failedCancel();
                    break;
                default:
                    break;
            }
        }
    }

    private void failedCancel() {
        Intent intent = new Intent();
        if (from == 0) {//BalanceActivity
            intent.setClass(this, BalanceActivity.class);
            intent.putExtra(BalanceActivity.EXTRA_FROM, 1);
            intent.putExtra(BalanceActivity.EXTRA_ERRCODE, -2);
        } else if (from == 1) {//OrderDetailsActivity
            intent.setClass(this, OrderDetailsActivity.class);
            intent.putExtra(OrderLogic.EXTRA_ORDERNUM, SPUtils.getInt(context, GlobalInfo.KEY_ORDER_NUM, 0));
            intent.putExtra(OrderDetailsActivity.EXTRA_FROM, 3);
            intent.putExtra(OrderDetailsActivity.EXTRA_ERRCODE, -2);
        } else if (from == 2) {//MyOrderActivity
            intent.setClass(this, MyOrderActivity.class);
            intent.putExtra(OrderDetailsActivity.EXTRA_FROM, 3);
            intent.putExtra(OrderDetailsActivity.EXTRA_ERRCODE, -2);
        } else if (from == 3) {//WaterTicketDetailActivity
            intent.setClass(this, WaterTicketDetailActivity.class);
            intent.putExtra(OrderDetailsActivity.EXTRA_FROM, 3);
            intent.putExtra(OrderDetailsActivity.EXTRA_ERRCODE, -2);
        }
        startActivity(intent);
        finish();
    }

    private void failed() {
        Intent intent = new Intent();
        if (from == 0) {//BalanceActivity
            intent.setClass(this, BalanceActivity.class);
            intent.putExtra(BalanceActivity.EXTRA_FROM, 1);
            intent.putExtra(BalanceActivity.EXTRA_ERRCODE, -1);
        } else if (from == 1) {//OrderDetailsActivity
            intent.setClass(this, OrderDetailsActivity.class);
            intent.putExtra(OrderLogic.EXTRA_ORDERNUM, SPUtils.getInt(context, GlobalInfo.KEY_ORDER_NUM, 0));
            intent.putExtra(OrderDetailsActivity.EXTRA_FROM, 3);
            intent.putExtra(OrderDetailsActivity.EXTRA_ERRCODE, -1);
        } else if (from == 2) {//MyOrderActivity
            intent.setClass(this, MyOrderActivity.class);
            intent.putExtra(OrderDetailsActivity.EXTRA_FROM, 3);
            intent.putExtra(OrderDetailsActivity.EXTRA_ERRCODE, -1);
        } else if (from == 3) {//WaterTicketDetailActivity
            intent.setClass(this, WaterTicketDetailActivity.class);
            intent.putExtra(OrderDetailsActivity.EXTRA_FROM, 3);
            intent.putExtra(OrderDetailsActivity.EXTRA_ERRCODE, -1);
        }
        startActivity(intent);
        finish();
    }

    private void checkPayState() {
        Intent intent = new Intent(OrderLogic.ACTION_GET_CHECKPAY);
        intent.putExtra(OrderLogic.EXTRA_ORDERNUM, SPUtils.getInt(context, GlobalInfo.KEY_ORDER_NUM, 0));
        sendAction(intent);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    protected void init(Bundle bundle) {

    }

    @Override
    protected void onUIEvent(int eventId, Bundle bundle) {
        String notice = bundle.getString(BaseLogic.EXTRA_ERROR);
        switch (eventId) {
            case Event.GET_PREPAY_CHECKPAY_SUCESS://WX支付查询订单状态成功事件
                Payment payment = (Payment) bundle.getSerializable(OrderLogic.EXTRA_ORDERINFO);
                boolean isWaterOrder = SPUtils.getBoolean(context, GlobalInfo.KEY_ISWATER, false);

                PayStatus status = payment.getPaymentStatus();
                Intent intent = new Intent();
                if (status.equals(PayStatus.已支付)) {//2已支付
                    SPUtils.saveBoolean(context, GlobalInfo.KEY_ISWATER, false);
                    if (isWaterOrder) {
                        intent.putExtra(WaterTicketActivity.EXTRA_ID, payment.getOrderId());
                        intent.setClass(this, WaterTicketActivity.class);
                    } else {
                        intent.setClass(this, OrderDetailsActivity.class);
                    }
                    intent.putExtra(OrderLogic.EXTRA_ORDERNUM, payment.getOrderId());
                    intent.putExtra(OrderDetailsActivity.EXTRA_FROM, 1);
                    startActivity(intent);
                    finish();
                }
                break;
            case Event.GET_PREPAY_CHECKPAY_FAILED://WX支付查询订单状态失败事件
                MyToast.show(this, notice);
                break;
            default:
                break;
        }
    }

    @Override
    protected void addEventListener() {
        App.getInstance().addListener(this,
                Event.GET_PREPAY_CHECKPAY_SUCESS, Event.GET_PREPAY_CHECKPAY_FAILED);
    }

    @Override
    protected void removeListener() {
        App.getInstance().removeListener(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
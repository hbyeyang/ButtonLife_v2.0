package com.anniu.shandiandaojia.wxapi;

import android.app.Activity;
import android.os.Bundle;

import com.anniu.shandiandaojia.R;
import com.anniu.shandiandaojia.db.jsondb.PrepayIdInfo;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class PayActivity extends Activity {

    public static String EXTRA_PAYINFO = "payinfo";
    final IWXAPI msgApi = WXAPIFactory.createWXAPI(this, null);
    private PrepayIdInfo prepayInfo;
    private PayReq req;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay);
        prepayInfo = (PrepayIdInfo) getIntent().getExtras().getSerializable(EXTRA_PAYINFO);

        req = new PayReq();
        msgApi.registerApp(prepayInfo.getAppid());
        genPayReq();
        sendPayReq();
    }

    private void sendPayReq() {
        msgApi.registerApp(prepayInfo.getAppid());
        msgApi.sendReq(req);
        finish();
    }

    private void genPayReq() {
        req.appId = prepayInfo.getAppid();
        req.partnerId = prepayInfo.getPartnerid();
        req.prepayId = prepayInfo.getPrepayid();
        req.packageValue = "Sign=WXPay";
        req.nonceStr = prepayInfo.getNoncestr();
        req.timeStamp = prepayInfo.getTimestamp();
        req.sign = prepayInfo.getSign();
    }
}

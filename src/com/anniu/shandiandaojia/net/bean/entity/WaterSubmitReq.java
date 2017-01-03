package com.anniu.shandiandaojia.net.bean.entity;

import com.anniu.shandiandaojia.base.ActivityMgr;
import com.anniu.shandiandaojia.db.jsondb.PaymentWay;
import com.anniu.shandiandaojia.net.bean.HttpReq;
import com.anniu.shandiandaojia.utils.GlobalInfo;
import com.anniu.shandiandaojia.utils.SPUtils;

/***
 * @author zxl
 * @ClassName: WaterSubmitReq
 * @Description: 购买水票
 * @date 2015年7月15日 下午2:52:02
 */
public class WaterSubmitReq extends HttpReq {
    private int goodsCode;
    private PaymentWay paymentWay;

    public WaterSubmitReq(int reqId, PaymentWay paymentWay, int goodsCode) {
        this.id = reqId;
        this.paymentWay = paymentWay;
        this.goodsCode = goodsCode;
    }

    public int getGoodsCode() {
        return goodsCode;
    }

    public void setGoodsCode(int goodsCode) {
        this.goodsCode = goodsCode;
    }

    public PaymentWay getPaymentWay() {
        return paymentWay;
    }

    public void setPaymentWay(PaymentWay paymentWay) {
        this.paymentWay = paymentWay;
    }

    @Override
    public String getParams() {
        return "shopId=" + SPUtils.getInt(ActivityMgr.getContext(), GlobalInfo.KEY_SHOPCODE, 0)
                + "&id=" + goodsCode + "&paymentWay=" + paymentWay;
    }

    @Override
    public String parseData(String str) {
        return null;
    }

}

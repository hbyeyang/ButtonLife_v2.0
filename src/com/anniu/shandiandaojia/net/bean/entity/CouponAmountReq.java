package com.anniu.shandiandaojia.net.bean.entity;

import com.anniu.shandiandaojia.net.bean.HttpReq;

/**
 * 购买水票返还的优惠券金额
 */
public class CouponAmountReq extends HttpReq {
    private int orderId;

    public CouponAmountReq(int reqId, int orderId) {
        this.id = reqId;
        this.orderId = orderId;
    }

    @Override
    public String getParams() {
        return "?orderId=" + orderId;
    }

    @Override
    public String parseData(String str) {
        return null;
    }

}

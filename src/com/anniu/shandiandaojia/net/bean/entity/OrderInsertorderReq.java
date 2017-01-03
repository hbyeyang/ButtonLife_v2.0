package com.anniu.shandiandaojia.net.bean.entity;

import com.anniu.shandiandaojia.base.ActivityMgr;
import com.anniu.shandiandaojia.db.jsondb.PaymentWay;
import com.anniu.shandiandaojia.net.bean.HttpReq;
import com.anniu.shandiandaojia.utils.GlobalInfo;
import com.anniu.shandiandaojia.utils.SPUtils;

/***
 * @author YY
 * @ClassName: OrderInsertorderReq
 * @Description: 普通商品订单支付
 * @date 2015年6月29日 下午7:00:54
 */
public class OrderInsertorderReq extends HttpReq {
    private int couponId;
    private PaymentWay paymentWay;
    private String postStartTime;
    private String postEndTime;
    private String remark;

    public OrderInsertorderReq(int reqId, int couponId, PaymentWay paymentWay, String postStartTime, String postEndTime, String remark) {
        this.id = reqId;
        this.couponId = couponId;
        this.paymentWay = paymentWay;
        this.postStartTime = postStartTime;
        this.postEndTime = postEndTime;
        this.remark = remark;
    }

    public int getCouponId() {
        return couponId;
    }

    public void setCouponId(int couponId) {
        this.couponId = couponId;
    }

    public PaymentWay getPaymentWay() {
        return paymentWay;
    }

    public void setPaymentWay(PaymentWay paymentWay) {
        this.paymentWay = paymentWay;
    }

    public String getPostStartTime() {
        return postStartTime;
    }

    public void setPostStartTime(String postStartTime) {
        this.postStartTime = postStartTime;
    }

    public String getPostEndTime() {
        return postEndTime;
    }

    public void setPostEndTime(String postEndTime) {
        this.postEndTime = postEndTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String getParams() {
        return "shopId=" + SPUtils.getInt(ActivityMgr.getContext(), GlobalInfo.KEY_SHOPCODE, 0) + "&couponId=" + couponId + "&paymentWay=" + paymentWay + "&postStartTime=" + postStartTime
                + "&postEndTime=" + postEndTime + "&remark=" + remark;
    }

    @Override
    public String parseData(String str) {
        return null;
    }

}

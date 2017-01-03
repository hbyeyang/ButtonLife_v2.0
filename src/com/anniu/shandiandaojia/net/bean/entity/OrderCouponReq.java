package com.anniu.shandiandaojia.net.bean.entity;

import com.anniu.shandiandaojia.base.ActivityMgr;
import com.anniu.shandiandaojia.net.bean.HttpReq;
import com.anniu.shandiandaojia.utils.GlobalInfo;
import com.anniu.shandiandaojia.utils.SPUtils;

import java.net.URLEncoder;

/**
 * @author YY
 * @ClassName: OrderCouponReq
 * @Description: 获取可用的优惠券
 * @date 2015年7月2日 上午10:22:08
 */
public class OrderCouponReq extends HttpReq {
    private double amount;
    private String status;

    public OrderCouponReq(int reqId, double amount, String status) {
        this.id = reqId;
        this.amount = amount;
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Override
    public String getParams() {
        return "?shopId=" + SPUtils.getInt(ActivityMgr.getContext(), GlobalInfo.KEY_SHOPCODE, 0) +
                "&amount=" + amount + "&status=" + URLEncoder.encode(status);
    }

    @Override
    public String parseData(String str) {
        return null;
    }

}

package com.anniu.shandiandaojia.net.bean.entity;

import com.anniu.shandiandaojia.net.bean.HttpReq;

/**
 * @author zxl
 * @ClassName: WXCheckPayReq
 * @Description: 微信支付获取支付状态
 * @date 2015年7月7日 下午7:55:25
 */
public class WXCheckPayReq extends HttpReq {
    private int orderId;

    public WXCheckPayReq(int id, int ordernum) {
        super();
        this.id = id;
        this.orderId = ordernum;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    @Override
    public String getParams() {
        return "?id=" + orderId;
    }

    @Override
    public String parseData(String str) {
        return null;
    }
}

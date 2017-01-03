package com.anniu.shandiandaojia.net.bean.entity;

import com.anniu.shandiandaojia.net.bean.HttpReq;

/**
 * @author zxl
 * @ClassName: PrepayIdReq
 * @Description: 获取prepayid请求
 * @date 2015年7月6日 下午2:12:49
 */
public class PrepayIdReq extends HttpReq {
    private int orderId;

    public PrepayIdReq(int id, int orderId) {
        super();
        this.id = id;
        this.orderId = orderId;
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

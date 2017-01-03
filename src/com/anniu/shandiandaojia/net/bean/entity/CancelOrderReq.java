package com.anniu.shandiandaojia.net.bean.entity;

import com.anniu.shandiandaojia.net.bean.HttpReq;

/**
 * @author YY
 * @ClassName: CancelOrderReq
 * @Description: 订单取消请求
 * @date 2015年6月9日 下午12:37:45
 */
public class CancelOrderReq extends HttpReq {
    private int orderId;
    private String note;

    public CancelOrderReq(int reqId, int orderId, String note) {
        this.id = reqId;
        this.orderId = orderId;
        this.note = note;
    }

    @Override
    public String getParams() {
        return "id=" + orderId + "&note=" + note;
    }

    @Override
    public String parseData(String str) {
        return null;
    }

}

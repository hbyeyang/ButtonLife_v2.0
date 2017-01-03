package com.anniu.shandiandaojia.net.bean.entity;

import com.anniu.shandiandaojia.net.bean.HttpReq;

/**
 * @author YY
 * @ClassName: ConfirmOrderReq
 * @Description: 确认订单请求
 * @date 2015年6月9日 下午12:39:55
 */
public class ConfirmOrderReq extends HttpReq {
    private int orderNum;
    private String recTime;

    public ConfirmOrderReq(int reqid, int orderNum, String recTime) {
        super();
        this.id = reqid;
        this.orderNum = orderNum;
        this.recTime = recTime;
    }

    public int getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(int orderNum) {
        this.orderNum = orderNum;
    }

    public String getRecTime() {
        return recTime;
    }

    public void setRecTime(String recTime) {
        this.recTime = recTime;
    }

    @Override
    public String getParams() {
        return "id=" + orderNum + "&time=" + recTime;
    }

    @Override
    public String parseData(String str) {
        return null;
    }

}

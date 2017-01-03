package com.anniu.shandiandaojia.net.bean.entity;

import com.anniu.shandiandaojia.net.bean.HttpReq;

/**
 * @author YY
 * @ClassName: OrderDetails
 * @Description: 订单详情
 * @date 2015年6月9日 下午1:14:21
 */
public class OrderDetails extends HttpReq {
    private int orderNum;

    public OrderDetails(int reqid, int orderNum) {
        super();
        this.id = reqid;
        this.orderNum = orderNum;
    }

    public int getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(int orderNum) {
        this.orderNum = orderNum;
    }

    @Override
    public String getParams() {
        return "?id=" + orderNum;
    }

    @Override
    public String parseData(String str) {
        return null;
    }

}

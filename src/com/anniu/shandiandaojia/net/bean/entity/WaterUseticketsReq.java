package com.anniu.shandiandaojia.net.bean.entity;

import com.anniu.shandiandaojia.net.bean.HttpReq;

/***
 * @author YY
 * @ClassName: WaterUseticketsReq
 * @Description: 获取一键送水界面提交订单页面数据
 * @date 2015年6月17日 下午6:29:24
 */
public class WaterUseticketsReq extends HttpReq {

    private int ticketId;

    public WaterUseticketsReq(int reqid, int ticketId) {
        super();
        this.id = reqid;
        this.ticketId = ticketId;
    }

    public int getTicketId() {
        return ticketId;
    }

    public void setTicketId(int ticketId) {
        this.ticketId = ticketId;
    }

    @Override
    public String getParams() {
        return "?ticketId=" + ticketId;
    }

    @Override
    public String parseData(String str) {
        return null;
    }

}

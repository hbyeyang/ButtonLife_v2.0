package com.anniu.shandiandaojia.net.bean.entity;

import com.anniu.shandiandaojia.net.bean.HttpReq;

/**
 * @author YY
 * @ClassName: TicketBuyWaterReq
 * @Description: 用水票购买水
 * @date 2015年6月29日 下午3:45:51
 */
public class TicketBuyWaterReq extends HttpReq {
    private int shopId;
    private int ticketId;
    private int count;
    private String note;
    private String postStartTime;
    private String postEndTime;

    public TicketBuyWaterReq(int reqId, int shopId, int ticketId, int count, String note, String postStartTime, String postEndTime) {
        this.id = reqId;
        this.shopId = shopId;
        this.ticketId = ticketId;
        this.count = count;
        this.note = note;
        this.postStartTime = postStartTime;
        this.postEndTime = postEndTime;
    }

    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    public int getTicketId() {
        return ticketId;
    }

    public void setTicketId(int ticketId) {
        this.ticketId = ticketId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getPostEndTime() {
        return postEndTime;
    }

    public void setPostEndTime(String postEndTime) {
        this.postEndTime = postEndTime;
    }

    public String getPostStartTime() {
        return postStartTime;
    }

    public void setPostStartTime(String postStartTime) {
        this.postStartTime = postStartTime;
    }

    @Override
    public String getParams() {
        return "shopId=" + shopId + "&ticketId=" + ticketId
                + "&count=" + count + "&note=" + note
                + "&postStartTime=" + postStartTime + "&postEndTime=" + postEndTime;
    }

    @Override
    public String parseData(String str) {
        return null;
    }

}

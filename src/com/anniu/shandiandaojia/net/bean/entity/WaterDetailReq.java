package com.anniu.shandiandaojia.net.bean.entity;

import com.anniu.shandiandaojia.net.bean.HttpReq;

/**
 * @author YY
 * @ClassName: WaterDetailReq
 * @Description: 获取水票详情
 * @date 2015年6月17日 下午2:29:22
 */
public class WaterDetailReq extends HttpReq {

    private int goodId;

    public WaterDetailReq(int reqid, int goodId) {
        super();
        this.id = reqid;
        this.goodId = goodId;
    }

    public int getGoodId() {
        return goodId;
    }

    public void setGoodId(int goodId) {
        this.goodId = goodId;
    }

    @Override
    public String getParams() {
        return "?id=" + goodId;
    }

    @Override
    public String parseData(String str) {
        return null;
    }

}

package com.anniu.shandiandaojia.net.bean.entity;

import com.anniu.shandiandaojia.net.bean.HttpReq;

/**
 * @author zxl
 * @ClassName: GoodDeleteReq
 * @Description: 删除购物车中一条商品纪录
 * @date 2015年6月9日 下午5:13:10
 */
public class GoodDeleteReq extends HttpReq {

    /**
     * 每页大小
     */
    private int goodId;

    public GoodDeleteReq(int reqId, int goodId) {
        super();
        this.id = reqId;
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
        return "id=" + goodId;
    }

    @Override
    public String parseData(String str) {
        return null;
    }
}

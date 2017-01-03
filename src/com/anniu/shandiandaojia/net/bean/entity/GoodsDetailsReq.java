package com.anniu.shandiandaojia.net.bean.entity;

import com.anniu.shandiandaojia.net.bean.HttpReq;

/**
 * @author zxl
 * @ClassName: GoodsDetailsReq
 * @Description: 商品详情请求
 * @date 2015年6月3日 下午8:06:15
 */
public class GoodsDetailsReq extends HttpReq {

    /**
     * 商店商品标识id
     */
    private int goodsid;

    public GoodsDetailsReq(int reqid, int id) {
        super();
        this.id = reqid;
        this.goodsid = id;
    }

    public int getGoodsid() {
        return goodsid;
    }

    public void setGoodsid(int goodsid) {
        this.goodsid = goodsid;
    }

    @Override
    public String getParams() {
        return "?id=" + goodsid;
    }

    @Override
    public String parseData(String str) {
        return null;
    }
}

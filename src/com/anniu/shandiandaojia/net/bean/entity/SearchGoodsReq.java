package com.anniu.shandiandaojia.net.bean.entity;

import com.anniu.shandiandaojia.net.bean.HttpReq;

/**
 * @author YY
 * @ClassName: SearchGoodsReq
 * @Description: 搜索商品
 * @date 2015年6月25日 下午8:28:05
 */
public class SearchGoodsReq extends HttpReq {
    private int shopId;
    private String keywords;

    public SearchGoodsReq(int reqId, int shopId, String keywords) {
        this.id = reqId;
        this.shopId = shopId;
        this.keywords = keywords;
    }

    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    @Override
    public String getParams() {
        return "shopId=" + shopId + "&keywords=" + keywords;
    }

    @Override
    public String parseData(String str) {
        return null;
    }

}

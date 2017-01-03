package com.anniu.shandiandaojia.net.bean.entity;

import com.anniu.shandiandaojia.net.bean.HttpReq;

/**
 * @author zxl
 * @ClassName: TabCategoryGoodsReq
 * @Description: 分类页请求
 * @date 2015年6月11日 下午4:16:04
 */
public class TabCategoryGoodsReq extends HttpReq {

    private int shopId;

    public TabCategoryGoodsReq(int reqId, int shopcode) {
        super();
        this.id = reqId;
        this.shopId = shopcode;
    }


    public int getShopcode() {
        return shopId;
    }

    public void setShopcode(int shopcode) {
        this.shopId = shopcode;
    }

    @Override
    public String getParams() {
        return "?shopId=" + shopId;
    }

    @Override
    public String parseData(String str) {
        return null;
    }
}

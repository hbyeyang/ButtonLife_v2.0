package com.anniu.shandiandaojia.net.bean.entity;

import com.anniu.shandiandaojia.net.bean.HttpReq;

/**
 * @author zxl
 * @ClassName: ShopInfoReq
 * @Description: 获取商铺首页信息
 * @date 2015年6月2日 上午11:06:43
 */
public class ShopInfoReq extends HttpReq {

    /**
     * 选择商店的id
     */
    private int shopId;

    public ShopInfoReq(int reqId, int shopId) {
        super();
        this.id = reqId;
        this.shopId = shopId;
    }

    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    @Override
    public String getParams() {
        return "?id=" + shopId;
    }

    @Override
    public String parseData(String str) {
        return null;
    }
}

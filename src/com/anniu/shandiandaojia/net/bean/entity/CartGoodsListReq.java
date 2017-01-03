package com.anniu.shandiandaojia.net.bean.entity;

import com.anniu.shandiandaojia.base.ActivityMgr;
import com.anniu.shandiandaojia.net.bean.HttpReq;
import com.anniu.shandiandaojia.utils.GlobalInfo;
import com.anniu.shandiandaojia.utils.SPUtils;

/**
 * @author zxl
 * @ClassName: CartGoodsListReq
 * @Description: 获取购物车商品列表
 * @date 2015年6月8日 下午8:35:09
 */
public class CartGoodsListReq extends HttpReq {

    public CartGoodsListReq(int reqId) {
        super();
        this.id = reqId;
    }

    @Override
    public String getParams() {
        return "?shopId=" + SPUtils.getInt(ActivityMgr.getContext(), GlobalInfo.KEY_SHOPCODE, 0);
    }

    @Override
    public String parseData(String str) {
        return null;
    }
}

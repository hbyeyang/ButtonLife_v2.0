package com.anniu.shandiandaojia.net.bean.entity;

import com.anniu.shandiandaojia.base.ActivityMgr;
import com.anniu.shandiandaojia.net.bean.HttpReq;
import com.anniu.shandiandaojia.utils.GlobalInfo;
import com.anniu.shandiandaojia.utils.SPUtils;

/**
 * @author zxl
 * @ClassName: ShoppingCartInfoReq
 * @Description:购物车页面信息
 * @date 2015年6月9日 下午2:48:26
 */
public class ShoppingCartInfoReq extends HttpReq {
    public ShoppingCartInfoReq(int reqId) {
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

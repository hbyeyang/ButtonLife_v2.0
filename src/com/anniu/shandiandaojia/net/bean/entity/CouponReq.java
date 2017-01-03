package com.anniu.shandiandaojia.net.bean.entity;

import com.anniu.shandiandaojia.base.ActivityMgr;
import com.anniu.shandiandaojia.net.bean.HttpReq;
import com.anniu.shandiandaojia.utils.GlobalInfo;
import com.anniu.shandiandaojia.utils.SPUtils;

import java.net.URLEncoder;

/**
 * @author YY
 * @ClassName: CouponReq
 * @Description: 获取票的请求
 * @date 2015年6月15日 下午3:46:47
 */
public class CouponReq extends HttpReq {

    public CouponReq(int reqid) {
        this.id = reqid;
    }

    @Override
    public String getParams() {
        return "?shopId=" + SPUtils.getInt(ActivityMgr.getContext(), GlobalInfo.KEY_SHOPCODE, 0)
                + "&status=" + URLEncoder.encode("未使用");
    }

    @Override
    public String parseData(String str) {
        return null;
    }

}

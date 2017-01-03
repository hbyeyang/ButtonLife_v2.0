package com.anniu.shandiandaojia.net.bean.entity;

import com.anniu.shandiandaojia.base.ActivityMgr;
import com.anniu.shandiandaojia.net.bean.HttpReq;
import com.anniu.shandiandaojia.utils.GlobalInfo;
import com.anniu.shandiandaojia.utils.SPUtils;

/**
 * @author YY
 * @ClassName: FindWaterTicketReq
 * @Description: 获取所有水票
 * @date 2015年6月29日 下午1:54:30
 */
public class FindWaterTicketReq extends HttpReq {
    public FindWaterTicketReq(int reqid) {
        super();
        this.id = reqid;
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

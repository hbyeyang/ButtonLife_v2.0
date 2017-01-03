package com.anniu.shandiandaojia.net.bean.entity;

import com.anniu.shandiandaojia.base.ActivityMgr;
import com.anniu.shandiandaojia.net.bean.HttpReq;
import com.anniu.shandiandaojia.utils.GlobalInfo;
import com.anniu.shandiandaojia.utils.SPUtils;

/***
 * @author YY
 * @ClassName: AckorderReq
 * @Description: 结算请求
 * @date 2015年6月11日 下午8:27:39
 */
public class AckorderReq extends HttpReq {

    public AckorderReq(int reqid) {
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

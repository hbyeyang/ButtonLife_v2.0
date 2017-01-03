package com.anniu.shandiandaojia.net.bean.entity;

import com.anniu.shandiandaojia.base.ActivityMgr;
import com.anniu.shandiandaojia.net.bean.HttpReq;
import com.anniu.shandiandaojia.utils.GlobalInfo;
import com.anniu.shandiandaojia.utils.SPUtils;

/***
 * @author YY
 * @ClassName: WaterFindReq
 * @Description: 初始化一键送水界面
 * @date 2015年6月16日 上午11:04:42
 */
public class WaterFindReq extends HttpReq {

    public WaterFindReq(int reqid) {
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

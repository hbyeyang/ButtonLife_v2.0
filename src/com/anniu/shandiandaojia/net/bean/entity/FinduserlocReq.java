package com.anniu.shandiandaojia.net.bean.entity;

import com.anniu.shandiandaojia.net.bean.HttpReq;

/**
 * @author YY
 * @ClassName: FinduserlocReq
 * @Description: 获取个人中心我的地址
 * @date 2015年6月8日 上午10:46:38
 */
public class FinduserlocReq extends HttpReq {

    public FinduserlocReq(int reqid) {
        super();
        this.id = reqid;
    }

    @Override
    public String getParams() {
        return null;
    }

    @Override
    public String parseData(String str) {
        return null;
    }

}

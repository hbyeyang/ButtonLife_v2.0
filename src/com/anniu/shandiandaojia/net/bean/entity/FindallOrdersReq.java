package com.anniu.shandiandaojia.net.bean.entity;

import com.anniu.shandiandaojia.net.bean.HttpReq;

/**
 * @author YY
 * @ClassName: FindallOrdersReq
 * @Description: 获取用户订单请求
 * @date 2015年6月5日 下午1:03:42
 */
public class FindallOrdersReq extends HttpReq {

    public FindallOrdersReq(int reqid) {
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

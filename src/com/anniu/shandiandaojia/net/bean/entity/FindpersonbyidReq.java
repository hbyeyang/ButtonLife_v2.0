package com.anniu.shandiandaojia.net.bean.entity;

import com.anniu.shandiandaojia.net.bean.HttpReq;

/***
 * @author YY
 * @ClassName: FindpersonbyidReq
 * @Description: 获取个人中心详情
 * @date 2015年6月4日 下午4:47:36
 */
public class FindpersonbyidReq extends HttpReq {

    public FindpersonbyidReq(int reqid) {
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

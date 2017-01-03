package com.anniu.shandiandaojia.net.bean.entity;

import com.anniu.shandiandaojia.net.bean.HttpReq;

/***
 * 登出
 */
public class LoginOutReq extends HttpReq {


    public LoginOutReq(int reqid) {
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

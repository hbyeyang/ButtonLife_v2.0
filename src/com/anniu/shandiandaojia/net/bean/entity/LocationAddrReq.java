package com.anniu.shandiandaojia.net.bean.entity;

import com.anniu.shandiandaojia.net.bean.HttpReq;

import java.net.URLEncoder;

/**
 * @author zxl
 * @ClassName: LocationAddrReq
 * @Description: 通过地址定位请求
 * @date 2015年5月30日 下午5:41:48
 */

public class LocationAddrReq extends HttpReq {

    /**
     * 用户输入的地址
     */
    private String address;

    public LocationAddrReq(int reqId, String address) {
        super();
        this.id = reqId;
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String getParams() {
        return "?address=" + URLEncoder.encode(address);
    }

    @Override
    public String parseData(String str) {
        return null;
    }
}

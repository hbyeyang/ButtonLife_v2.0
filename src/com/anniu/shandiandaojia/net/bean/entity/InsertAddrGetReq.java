package com.anniu.shandiandaojia.net.bean.entity;

import com.anniu.shandiandaojia.net.bean.HttpReq;

public class InsertAddrGetReq extends HttpReq {

    private int addressId;

    public InsertAddrGetReq(int reqid, int addressId) {
        super();
        this.id = reqid;
        this.addressId = addressId;
    }

    public int getAddressId() {
        return addressId;
    }

    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }

    @Override
    public String getParams() {
        return "id=" + addressId;
    }

    @Override
    public String parseData(String str) {
        return null;
    }

}

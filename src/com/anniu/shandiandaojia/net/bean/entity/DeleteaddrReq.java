package com.anniu.shandiandaojia.net.bean.entity;

import com.anniu.shandiandaojia.net.bean.HttpReq;

/**
 * @author YY
 * @ClassName: DeleteaddrReq
 * @Description: 删除用户信息请求
 * @date 2015年6月8日 下午8:45:26
 */
public class DeleteaddrReq extends HttpReq {

    private int addressId;

    public DeleteaddrReq(int reqid, int addressId) {
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

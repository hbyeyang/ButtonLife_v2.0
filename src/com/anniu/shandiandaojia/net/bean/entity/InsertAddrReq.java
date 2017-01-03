package com.anniu.shandiandaojia.net.bean.entity;

import com.anniu.shandiandaojia.net.bean.HttpReq;

public class InsertAddrReq extends HttpReq {

    private String contactName;// 联系人姓名 字符串
    private String contactTel;// 联系电话  字符串
    private String consignDistrict;// 所在区域  字符串
    private String consignAddress;// 详细地址  字符串
    private boolean isDefault;// 是否默认  true, false

    public InsertAddrReq(int reqId, String contactName, String contactTel,
                         String consignDistrict, String consignAddress, boolean isDefault) {
        this.id = reqId;
        this.contactName = contactName;
        this.contactTel = contactTel;
        this.consignDistrict = consignDistrict;
        this.consignAddress = consignAddress;
        this.isDefault = isDefault;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactTel() {
        return contactTel;
    }

    public void setContactTel(String contactTel) {
        this.contactTel = contactTel;
    }

    public String getConsignDistrict() {
        return consignDistrict;
    }

    public void setConsignDistrict(String consignDistrict) {
        this.consignDistrict = consignDistrict;
    }

    public String getConsignAddress() {
        return consignAddress;
    }

    public void setConsignAddress(String consignAddress) {
        this.consignAddress = consignAddress;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setIsDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    @Override
    public String getParams() {
        return "contactName=" + contactName + "&contactTel=" + contactTel
                + "&consignDistrict=" + consignDistrict + "&consignAddress="
                + consignAddress + "&isDefault=" + isDefault;
    }

    @Override
    public String parseData(String str) {
        return null;
    }

}

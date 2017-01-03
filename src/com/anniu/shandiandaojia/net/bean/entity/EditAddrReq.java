package com.anniu.shandiandaojia.net.bean.entity;

import com.anniu.shandiandaojia.net.bean.HttpReq;

/**
 * @author YY
 * @ClassName: EditAddrReq
 * @Description: 修改用户收货地址请求--修改
 * @date 2015年6月8日 下午3:21:18
 */
public class EditAddrReq extends HttpReq {
    private int addressId;// 地址 ID 整数
    private String contactName;// 联系人姓名 字符串
    private String contactTel;// 联系电话  字符串
    private String consignAddress;// 详细地址  字符串
    private String consignDistrict;// 所在区域  字符串
    private boolean isDefault;// 是否默认  true, false

    public EditAddrReq(int reqId, int addressId, String contactName, String contactTel,
                       String consignAddress, String consignDistrict, boolean isDefault) {
        this.id = reqId;
        this.addressId = addressId;
        this.contactName = contactName;
        this.contactTel = contactTel;
        this.consignAddress = consignAddress;
        this.consignDistrict = consignDistrict;
        this.isDefault = isDefault;
    }

    public int getAddressId() {
        return addressId;
    }

    public void setAddressId(int addressId) {
        this.addressId = addressId;
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

    public String getConsignAddress() {
        return consignAddress;
    }

    public void setConsignAddress(String consignAddress) {
        this.consignAddress = consignAddress;
    }

    public String getConsignDistrict() {
        return consignDistrict;
    }

    public void setConsignDistrict(String consignDistrict) {
        this.consignDistrict = consignDistrict;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setIsDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    @Override
    public String getParams() {
        return "id=" + addressId + "&contactName=" + contactName + "&contactTel=" + contactTel
                + "&consignAddress=" + consignAddress + "&consignDistrict="
                + consignDistrict + "&isDefault=" + isDefault;
    }

    @Override
    public String parseData(String str) {
        return null;
    }

}

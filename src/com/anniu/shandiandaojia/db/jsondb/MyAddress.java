package com.anniu.shandiandaojia.db.jsondb;

import java.io.Serializable;

/**
 * @author YY
 * @ClassName:MyAddress
 * @Description:MyAddress
 * @date 2015年6月8日 下午12:28:17
 */
public class MyAddress implements Serializable {

    private static final long serialVersionUID = -3587094999907260779L;
    private int id;// 地址 ID
    private int userId;  // 用户 ID
    private String userName; // 用户名
    private String contactName;  // 联系人姓名
    private String contactTel; // 联系电话
    private String consignDistrict; // 所在区域
    private String consignAddress;// 详细地址
    private boolean isDefault; // 是否默认

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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
}

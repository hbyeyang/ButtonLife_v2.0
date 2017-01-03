package com.anniu.shandiandaojia.db.jsondb;

import java.io.Serializable;

/**
 * @author YY
 * @ClassName: Coupon
 * @Description: 代金券
 * @date 2015年6月5日 下午1:52:23
 */
public class Coupon implements Serializable {
    private static final long serialVersionUID = -8719068902354329269L;

    private int id;
    private int userId;
    private int shopId;
    private String userName;
    private double amount;//优惠卷金额
    private double minUseAmount;//最低起用金额
    private String source;//最低起用金额
    private String expireTime;//失效过期时间
    private String status;//优惠卷状态
    private String shopName;

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

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

    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getMinUseAmount() {
        return minUseAmount;
    }

    public void setMinUseAmount(double minUseAmount) {
        this.minUseAmount = minUseAmount;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(String expireTime) {
        this.expireTime = expireTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

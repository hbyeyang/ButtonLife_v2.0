package com.anniu.shandiandaojia.db.jsondb;

import java.io.Serializable;

/**
 * 订单中的商品
 */
public class OrderGoods extends Goods implements Serializable {
    private int orderId;
    private String orderNo;
    private int userId;
    private String userName;
    private String goodsName;
    private int goodsCount;
    private double goodsPrice;
    private double amount;
    private double originalUnitPrice;
    private double originalAmount;

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public Integer getGoodsCount() {
        return goodsCount;
    }

    public void setGoodsCount(Integer goodsCount) {
        this.goodsCount = goodsCount;
    }

    public Double getGoodsPrice() {
        return goodsPrice;
    }

    public void setGoodsPrice(Double goodsPrice) {
        this.goodsPrice = goodsPrice;
    }


    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getOriginalUnitPrice() {
        return originalUnitPrice;
    }

    public void setOriginalUnitPrice(Double originalUnitPrice) {
        this.originalUnitPrice = originalUnitPrice;
    }

    public Double getOriginalAmount() {
        return originalAmount;
    }

    public void setOriginalAmount(Double originalAmount) {
        this.originalAmount = originalAmount;
    }
}

package com.anniu.shandiandaojia.db.jsondb;

import java.io.Serializable;

/***
 * @author zxl
 * @ClassName: BottomStatusBar
 * @Description: 底部状态栏
 * @date 2015年6月2日 下午2:18:42
 */
public class BottomStatusBar implements Serializable {

    private static final long serialVersionUID = -1271759838712168711L;

    private int userId;
    private int shopId;
    //总价
    private double amount;
    //免费配送价
    private double freePostPrice;
    //商品数量
    private int goodsCount;
    //起送价
    private double lowestPostPrice;
    //运费
    private double postFee;

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

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getFreePostPrice() {
        return freePostPrice;
    }

    public void setFreePostPrice(double freePostPrice) {
        this.freePostPrice = freePostPrice;
    }

    public int getGoodsCount() {
        return goodsCount;
    }

    public void setGoodsCount(int goodsCount) {
        this.goodsCount = goodsCount;
    }

    public double getLowestPostPrice() {
        return lowestPostPrice;
    }

    public void setLowestPostPrice(double lowestPostPrice) {
        this.lowestPostPrice = lowestPostPrice;
    }

    public double getPostFee() {
        return postFee;
    }

    public void setPostFee(double postFee) {
        this.postFee = postFee;
    }
}

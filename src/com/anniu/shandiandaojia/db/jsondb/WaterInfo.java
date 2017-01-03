package com.anniu.shandiandaojia.db.jsondb;

import java.io.Serializable;

/**
 * @author YY
 * @ClassName: WaterTicketInfo
 * @Description: 水票信息
 * @date 2015年6月16日 下午3:53:40
 */
public class WaterInfo implements Serializable {

    private static final long serialVersionUID = 5833972777958275612L;
    private int id;
    private int goodsId;
    private String goodsName;
    private int userId;
    private String userName;
    private int shopId;
    private String shopName;//"按钮超市",
    private String pictureUrl;//水图片
    private String description;//水简介图片
    private double ticketPrice;//水票价格
    private double couponAmount;//返优惠卷金额
    private int waterCount;//水票数量

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(int goodsId) {
        this.goodsId = goodsId;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
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

    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public double getTicketPrice() {
        return ticketPrice;
    }

    public void setTicketPrice(double ticketPrice) {
        this.ticketPrice = ticketPrice;
    }

    public double getCouponAmount() {
        return couponAmount;
    }

    public void setCouponAmount(double couponAmount) {
        this.couponAmount = couponAmount;
    }

    public int getWaterCount() {
        return waterCount;
    }

    public void setWaterCount(int waterCount) {
        this.waterCount = waterCount;
    }
}

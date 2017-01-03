package com.anniu.shandiandaojia.db.jsondb;

import java.io.Serializable;

/***
 * @author zxl
 * @ClassName: BargainGoods
 * @Description: 商品
 * @date 2015年6月2日 下午2:04:07
 */
public class Goods implements Serializable {
    private int id;
    private String no;
    private String name;
    private String fullName;
    private int typeId;
    private String typeName;
    private int shopId;
    private String shopName;
    private int goodsId;
    private double price;
    private double originalPrice;
    private String unit;
    private int groupCount;
    private String thumbUrl;
    private String pictureUrl;
    private String description;
    private int stockCount;
    private int soldCount;
    private String upTime;
    private boolean onSale;
    private int limitCount;
    private double couponAmount;
    private boolean isSelfProduct;
    private double selfProductSubsidy;
    private double priceSubsidy;
    private double totalSubsidy;
    private String status;
    private int cartGoodsCount;

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public void setShopId(Integer shopId) {
        this.shopId = shopId;
    }

    public Integer getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Integer goodsId) {
        this.goodsId = goodsId;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Integer getGroupCount() {
        return groupCount;
    }

    public void setGroupCount(Integer groupCount) {
        this.groupCount = groupCount;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStockCount(Integer stockCount) {
        this.stockCount = stockCount;
    }

    public void setSoldCount(Integer soldCount) {
        this.soldCount = soldCount;
    }

    public void setLimitCount(Integer limitCount) {
        this.limitCount = limitCount;
    }

    public Double getCouponAmount() {
        return couponAmount;
    }

    public void setCouponAmount(Double couponAmount) {
        this.couponAmount = couponAmount;
    }

    public Boolean getIsSelfProduct() {
        return isSelfProduct;
    }

    public void setIsSelfProduct(Boolean isSelfProduct) {
        this.isSelfProduct = isSelfProduct;
    }

    public Double getSelfProductSubsidy() {
        return selfProductSubsidy;
    }

    public void setSelfProductSubsidy(Double selfProductSubsidy) {
        this.selfProductSubsidy = selfProductSubsidy;
    }

    public Double getPriceSubsidy() {
        return priceSubsidy;
    }

    public void setPriceSubsidy(Double priceSubsidy) {
        this.priceSubsidy = priceSubsidy;
    }

    public Double getTotalSubsidy() {
        return totalSubsidy;
    }

    public void setTotalSubsidy(Double totalSubsidy) {
        this.totalSubsidy = totalSubsidy;
    }

    public Integer getCartGoodsCount() {
        return cartGoodsCount;
    }

    public void setCartGoodsCount(Integer cartGoodsCount) {
        this.cartGoodsCount = cartGoodsCount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(Double originalPrice) {
        this.originalPrice = originalPrice;
    }

    public int getStockCount() {
        return stockCount;
    }

    public void setStockCount(int stockCount) {
        this.stockCount = stockCount;
    }

    public int getSoldCount() {
        return soldCount;
    }

    public void setSoldCount(int soldCount) {
        this.soldCount = soldCount;
    }

    public String getUpTime() {
        return upTime;
    }

    public void setUpTime(String upTime) {
        this.upTime = upTime;
    }

    public Boolean getOnSale() {
        return onSale;
    }

    public void setOnSale(Boolean onSale) {
        this.onSale = onSale;
    }

    public int getLimitCount() {
        return limitCount;
    }

    public void setLimitCount(int limitCount) {
        this.limitCount = limitCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

}

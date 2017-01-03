package com.anniu.shandiandaojia.db.jsondb;

import java.io.Serializable;
import java.util.List;

public class HomeViewModel implements Serializable {

    /**
     * 商店信息
     */
    private ShopInfo shop;
    /**
     * Banner List
     */
    private List<Banner> bannerList;
    /**
     * 打折（促销）商品
     */
    private List<Goods> discountGoodsList;
    /**
     * 商品分类（TOP 3）
     */
    private List<Category> goodsTypeList;
    /**
     * 购物车中的商品列表
     */
    private List<CartGoods> cartGoodsList;
    /**
     * 配送信息
     */
    private BottomStatusBar post;
    /**
     * 水票数量
     */
    private Integer waterCardCount;

    private WaterInfo userTicket;

    public WaterInfo getUserTicket() {
        return userTicket;
    }

    public void setUserTicket(WaterInfo userTicket) {
        this.userTicket = userTicket;
    }

    public ShopInfo getShop() {
        return shop;
    }

    public void setShop(ShopInfo shop) {
        this.shop = shop;
    }

    public List<Banner> getBannerList() {
        return bannerList;
    }

    public void setBannerList(List<Banner> bannerList) {
        this.bannerList = bannerList;
    }

    public List<Goods> getDiscountGoodsList() {
        return discountGoodsList;
    }

    public void setDiscountGoodsList(List<Goods> discountGoodsList) {
        this.discountGoodsList = discountGoodsList;
    }

    public List<Category> getGoodsTypeList() {
        return goodsTypeList;
    }

    public void setGoodsTypeList(List<Category> goodsTypeList) {
        this.goodsTypeList = goodsTypeList;
    }

    public List<CartGoods> getCartGoodsList() {
        return cartGoodsList;
    }

    public void setCartGoodsList(List<CartGoods> cartGoodsList) {
        this.cartGoodsList = cartGoodsList;
    }

    public BottomStatusBar getPost() {
        return post;
    }

    public void setPost(BottomStatusBar post) {
        this.post = post;
    }

    public Integer getWaterCardCount() {
        return waterCardCount;
    }

    public void setWaterCardCount(Integer waterCardCount) {
        this.waterCardCount = waterCardCount;
    }
}
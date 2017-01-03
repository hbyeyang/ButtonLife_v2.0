package com.anniu.shandiandaojia.db.jsondb;

import java.io.Serializable;
import java.util.List;

public class GoodsViewModel implements Serializable {

    private Goods goods;
    private List<Goods> relativeGoodsList;
    private List<CartGoods> cartGoodsList;
    private BottomStatusBar post;
    private ShopInfo shop;

    public ShopInfo getShop() {
        return shop;
    }

    public void setShop(ShopInfo shop) {
        this.shop = shop;
    }

    public Goods getGoods() {
        return goods;
    }

    public void setGoods(Goods goods) {
        this.goods = goods;
    }

    public List<Goods> getRelativeGoodsList() {
        return relativeGoodsList;
    }

    public void setRelativeGoodsList(List<Goods> relativeGoodsList) {
        this.relativeGoodsList = relativeGoodsList;
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
}
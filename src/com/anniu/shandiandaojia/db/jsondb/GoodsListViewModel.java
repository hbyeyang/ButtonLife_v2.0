package com.anniu.shandiandaojia.db.jsondb;

import java.io.Serializable;
import java.util.List;

public class GoodsListViewModel implements Serializable{

    /**
     * 商店
     */
    private ShopInfo shop;
    /**
     * 商品分类
     */
    private List<Category> goodsTypeList;
    /**
     * 商品集
     */
    private List<Goods> goodsList;
    /**
     * 购物车中的商品
     */
    private List<CartGoods> cartGoodsList;
    /**
     * 配送信息
     */
    private BottomStatusBar post;
    /**
     * 分页信息
     */
    private Paging paging;

    public ShopInfo getShop() {
        return shop;
    }

    public void setShop(ShopInfo shop) {
        this.shop = shop;
    }

    public List<Category> getGoodsTypeList() {
        return goodsTypeList;
    }

    public void setGoodsTypeList(List<Category> goodsTypeList) {
        this.goodsTypeList = goodsTypeList;
    }

    public List<Goods> getGoodsList() {
        return goodsList;
    }

    public void setGoodsList(List<Goods> goodsList) {
        this.goodsList = goodsList;
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

    public Paging getPaging() {
        return paging;
    }

    public void setPaging(Paging paging) {
        this.paging = paging;
    }
}
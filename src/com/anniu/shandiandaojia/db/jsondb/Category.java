package com.anniu.shandiandaojia.db.jsondb;

import java.io.Serializable;
import java.util.ArrayList;

/***
 * @author zxl
 * @ClassName: Category
 * @Description: 首页商品分类
 * @date 2015年6月3日 上午11:16:35
 */
public class Category implements Serializable {
    private static final long serialVersionUID = 8142657093680066131L;
    /***
     * 商品分类编号
     */
    private int id;
    /***
     * 商品分类名称
     */
    private String name;
    /***
     * 分类商品列表
     */
    private ArrayList<Goods> goodsList = new ArrayList<Goods>();

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

    public ArrayList<Goods> getGoodsList() {
        return goodsList;
    }

    public void setGoodsList(ArrayList<Goods> goodsList) {
        this.goodsList = goodsList;
    }
}

package com.anniu.shandiandaojia.net.bean.entity;

import com.anniu.shandiandaojia.net.bean.HttpReq;

/**
 * 
 * @ClassName: WaterConfired
 * @Description: 购买水票的请求
 * @author YY
 * @date 2015年6月16日 下午2:19:34
 */
public class WaterConfired extends HttpReq {

	private int shopCode;
	private int goodsCode;
	private int userCode;

	public WaterConfired(int reqid, int shopCode, int goodsCode, int userCode) {
		super();
		this.id = reqid;
		this.shopCode = shopCode;
		this.goodsCode = goodsCode;
		this.userCode = userCode;
	}

	public int getShopCode() {
		return shopCode;
	}

	public void setShopCode(int shopCode) {
		this.shopCode = shopCode;
	}

	public int getGoodsCode() {
		return goodsCode;
	}

	public void setGoodsCode(int goodsCode) {
		this.goodsCode = goodsCode;
	}

	public int getUserCode() {
		return userCode;
	}

	public void setUserCode(int userCode) {
		this.userCode = userCode;
	}

	@Override
	public String getParams() {
		return "?shop_code=" + shopCode + "&goods_code=" + goodsCode + "&user_code=" + userCode;
	}

	@Override
	public String parseData(String str) {
		return null;
	}

}

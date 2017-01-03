package com.anniu.shandiandaojia.net.bean.entity;

import com.anniu.shandiandaojia.net.bean.HttpReq;

/**
 * 
 * @ClassName: WaterSelectedticketReq
 * @Description: 切换水票
 * @author YY
 * @date 2015年7月1日 下午4:10:08
 */
public class WaterSelectedticketReq extends HttpReq {

	private int ticCode;

	public WaterSelectedticketReq(int reqid, int ticCode) {
		super();
		this.id = reqid;
		this.ticCode = ticCode;
	}

	@Override
	public String getParams() {
		return "?tic_code=" + ticCode;
	}

	@Override
	public String parseData(String str) {
		return null;
	}

}

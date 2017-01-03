package com.anniu.shandiandaojia.net.bean;

import com.anniu.shandiandaojia.net.NetMgr.OnSocketRsp;


public abstract class SocketReq {
	public int id;
	private OnSocketRsp mOnSocketRsp;
	public abstract String getParams();
	public abstract String parseData(String str);
	
	public OnSocketRsp getOnSocketRsp() {
		return mOnSocketRsp;
	}
	public void setOnSocketRsp(OnSocketRsp mOnSocketRsp) {
		this.mOnSocketRsp = mOnSocketRsp;
	}
}

package com.anniu.shandiandaojia.net.bean;

import com.anniu.shandiandaojia.net.NetMgr.OnHttpRsp;


public abstract class HttpReq {
	public int id;
	public HttpRsp rsp;
	public OnHttpRsp mOnHttpRsp;
	public boolean isHttpPost = false;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public HttpRsp getRsp() {
		return rsp;
	}

	public void setRsp(HttpRsp rsp) {
		this.rsp = rsp;
	}

	public boolean isHttpPost() {
		return isHttpPost;
	}

	public void setHttpPost(boolean isHttpPost) {
		this.isHttpPost = isHttpPost;
	}

	public OnHttpRsp getOnHttpRsp() {
		return mOnHttpRsp;
	}

	public void setOnHttpRsp(OnHttpRsp mOnHttpRsp) {
		this.mOnHttpRsp = mOnHttpRsp;
	}

	public abstract String getParams();

	public abstract String parseData(String str);
}

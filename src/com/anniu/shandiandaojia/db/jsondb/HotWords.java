package com.anniu.shandiandaojia.db.jsondb;

import java.io.Serializable;
/***
 * @ClassName:  HotWords
 * @Description: 热词集合
 * @author YY
 * @date 2015年6月25日 下午5:35:36
 */
public class HotWords implements Serializable {

	private static final long serialVersionUID = -247710011229620776L;
	/**排序ID*/
	private int id;
	/**热词*/
	private String name;
	/**状态*/
	private int status;

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
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
}

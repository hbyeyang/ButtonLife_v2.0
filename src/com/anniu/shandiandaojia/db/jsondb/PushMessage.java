package com.anniu.shandiandaojia.db.jsondb;

import android.text.TextUtils;

import com.anniu.shandiandaojia.app.App;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 消息推送
 */
public class PushMessage implements Serializable {
	
	private static final DbUtils db = App.getInstance().getDbUtils();

	public static final int IS_LOOK_NO = 0;
	public static final int IS_LOOK_YES = 1;

	public static final int HREF_TYPE_RECEIVE = 0; // 待接收
	public static final int HREF_TYPE_CANCELED = 1; // 已取消
	
	private static final long serialVersionUID = -7998294000720531294L;
	@Id
	@Column(column = "id")
	private int id;				// 消息ID(自增) 
	
	@Column(column = "content")
	private String content;		//	内容
	
	@Column(column = "href_type")
	private int hrefType;		// 跳转类型
	
	@Column(column = "href_target")
	private String hrefTarget;	// 跳转目标
	
	@Column(column = "is_look")
	private int isLook;			// 是否查看 0:未查看，1：已查看
	
	@Column(column = "receive_date")
	private Date receiveDate;	// 接收日期
	
	/**
	 * 根据条件查询List 按接收时间降序排列
	 * @param pageIndex
	 * @param pageSize
	 * @param params
	 * @return
	 */
	public static List<PushMessage> getListOrderByReceiveTime(int pageIndex,int pageSize,Map<String,Object> params){
		List<PushMessage> pushMessageList = null;
		Selector sql = Selector.from(PushMessage.class).where("id", ">", "0");
		if(params != null) {
			if(!TextUtils.isEmpty(params.get("id").toString())){
				sql.and("id","=",params.get("id"));
			}
			if(!TextUtils.isEmpty(params.get("content").toString())){
				sql.and("content","=",params.get("content"));
			}
			if(!TextUtils.isEmpty(params.get("hrefType").toString())){
				sql.and("href_type","=",params.get("hrefType"));
			}
			if(!TextUtils.isEmpty(params.get("hrefTarget").toString())){
				sql.and("href_target","=",params.get("hrefTarget"));
			}
			if(!TextUtils.isEmpty(params.get("isLook").toString())){
				sql.and("is_look","=",params.get("isLook"));
			}
			if(!TextUtils.isEmpty(params.get("receiveDate").toString())){
				sql.and("receive_date","=",params.get("receiveDate"));
			}
		}
		sql.orderBy("receive_date",true);
		if(pageSize > 0) {
			sql.offset(pageIndex).limit(pageSize);
		}
		try {
			pushMessageList = db.findAll(sql);
		} catch (DbException e) {
			e.printStackTrace();
		}
		return pushMessageList;
	}
	
	/**
	 * 查询未读消息数
	 * @return
	 */
	public static long getUnReadMessageCount() {
		long count = 0;
		try {
			count = db.count(Selector.from(PushMessage.class).where("is_look", "=", IS_LOOK_NO));
		} catch (DbException e) {
			e.printStackTrace();
		}
		return count;
	}
	
	/**
	 * 保存或修改单条记录
	 * @param pushMessage
	 */
	public static void saveOrUpdate(PushMessage pushMessage) {
		if(pushMessage != null) {
			try {
				db.saveOrUpdate(pushMessage);
			} catch (DbException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 删除
	 * @param pushMessage
	 */
	public static void delete(List<PushMessage> pushMessage) {
		if(pushMessage != null && pushMessage.size() > 0) {
			try {
				db.deleteAll(pushMessage);
			} catch (DbException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("PushMessage[");
		result.append("content=").append(content);
		result.append(",hrefType=").append(hrefType);
		result.append(",hrefTarget=").append(hrefTarget);
		result.append(",isLook=").append(isLook);
		result.append(",receiveDate=").append(receiveDate);
		result.append("]");
		return result.toString();
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getHrefType() {
		return hrefType;
	}

	public void setHrefType(int hrefType) {
		this.hrefType = hrefType;
	}

	public String getHrefTarget() {
		return hrefTarget;
	}

	public void setHrefTarget(String hrefTarget) {
		this.hrefTarget = hrefTarget;
	}

	public int getIsLook() {
		return isLook;
	}

	public void setIsLook(int isLook) {
		this.isLook = isLook;
	}

	public Date getReceiveDate() {
		return receiveDate;
	}

	public void setReceiveDate(Date receiveDate) {
		this.receiveDate = receiveDate;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}

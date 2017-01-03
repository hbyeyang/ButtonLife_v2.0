package com.anniu.shandiandaojia.net.bean.entity;

import com.anniu.shandiandaojia.base.ActivityMgr;
import com.anniu.shandiandaojia.net.bean.HttpReq;
import com.anniu.shandiandaojia.utils.GlobalInfo;
import com.anniu.shandiandaojia.utils.SPUtils;

/**
 * 分类页第一次数据请求
 */
public class CategoryViewReq extends HttpReq {
    private int typeId; // 类型 ID，全部为：-1
    private int page; // 当前页码，从 1 开始
    private int limit; // 每页显示数量

    public CategoryViewReq(int reqId, int typeId, int page, int limit) {
        this.id = reqId;
        this.typeId = typeId;
        this.page = page;
        this.limit = limit;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    @Override
    public String getParams() {
        return "?shopId=" + SPUtils.getInt(ActivityMgr.getContext(), GlobalInfo.KEY_SHOPCODE, 0)
                + "&typeId=" + typeId + "&page=" + page + "&limit=" + limit;
    }

    @Override
    public String parseData(String str) {
        return null;
    }

}

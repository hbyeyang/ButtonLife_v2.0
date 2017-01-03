package com.anniu.shandiandaojia.net.bean.entity;

import com.anniu.shandiandaojia.base.ActivityMgr;
import com.anniu.shandiandaojia.net.bean.HttpReq;
import com.anniu.shandiandaojia.utils.GlobalInfo;
import com.anniu.shandiandaojia.utils.SPUtils;

public class CategoryGoodsReq extends HttpReq {
    private int typeId;
    private int page;
    private int limit;

    public CategoryGoodsReq(int reqId, int typeId, int page, int limit) {
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

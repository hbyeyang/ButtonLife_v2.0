package com.anniu.shandiandaojia.net.bean.entity;

import com.anniu.shandiandaojia.net.bean.HttpReq;

/**
 * @author YY
 * @ClassName: SearchHotsWordsReq
 * @Description: 获取搜索页面热词
 * @date 2015年6月25日 下午5:14:36
 */
public class SearchHotsWordsReq extends HttpReq {

    public SearchHotsWordsReq(int reqid) {
        super();
        this.id = reqid;
    }

    @Override
    public String getParams() {
        return null;
    }

    @Override
    public String parseData(String str) {
        return null;
    }

}

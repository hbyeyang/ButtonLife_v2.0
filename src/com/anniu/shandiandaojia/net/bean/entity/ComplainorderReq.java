package com.anniu.shandiandaojia.net.bean.entity;

import com.anniu.shandiandaojia.base.ActivityMgr;
import com.anniu.shandiandaojia.net.bean.HttpReq;
import com.anniu.shandiandaojia.utils.GlobalInfo;
import com.anniu.shandiandaojia.utils.SPUtils;
import com.anniu.shandiandaojia.utils.Utils;

/**
 * @author YY
 * @ClassName: ComplainorderReq
 * @Description: 投诉商铺请求
 * @date 2015年6月11日 下午3:37:49
 */
public class ComplainorderReq extends HttpReq {
    private String content; // 主要内容
    private String type; // 主要内容

    public ComplainorderReq(int reqId, String content, String type) {
        this.id = reqId;
        this.content = content;
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String getParams() {
        return "shopId=" + SPUtils.getInt(ActivityMgr.mContext, GlobalInfo.KEY_SHOPCODE, 0)
                + "&content=" + content + "&platform=Android" + "&version=" + Utils.version_name + "&type=" + type;
    }

    @Override
    public String parseData(String str) {
        return null;
    }

}

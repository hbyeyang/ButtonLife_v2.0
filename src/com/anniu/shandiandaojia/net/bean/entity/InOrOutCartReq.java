package com.anniu.shandiandaojia.net.bean.entity;

import com.anniu.shandiandaojia.net.bean.HttpReq;

/**
 * @author zxl
 * @ClassName: GoodsDetailsReq
 * @Description: 商品详情请求
 * @date 2015年6月3日 下午8:06:15
 */
public class InOrOutCartReq extends HttpReq {

    /**
     * 商店商品标识id
     */
    private int goodId;
    /**
     * 1=添加一个，2=减一个 3=根据可选参数num修改数量
     * PUSH,POP,SET
     */
    private String cmd;
    private int count;

    public InOrOutCartReq(int reqId, int id, int count, String cmd) {
        this.id = reqId;
        this.goodId = id;
        this.count = count;
        this.cmd = cmd;
    }

    public int getGoodId() {
        return goodId;
    }

    public void setGoodId(int goodId) {
        this.goodId = goodId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }


    @Override
    public String getParams() {
        return "id=" + goodId + "&count=" + count + "&cmd="
                + cmd;
    }

    @Override
    public String parseData(String str) {
        return null;
    }
}

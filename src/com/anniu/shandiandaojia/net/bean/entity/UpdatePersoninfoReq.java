package com.anniu.shandiandaojia.net.bean.entity;

import com.anniu.shandiandaojia.net.bean.HttpReq;

/**
 * @author YY
 * @ClassName: UpdatePersoninfo
 * @Description: 保存个人中心姓名性别的请求
 * @date 2015年6月5日 上午9:51:04
 */
public class UpdatePersoninfoReq extends HttpReq {
    private String userName;
    private boolean sex;
    private int usercode;

    public UpdatePersoninfoReq(int reqId, String userName, int usercode, boolean userSex) {
        this.id = reqId;
        this.userName = userName;
        this.sex = userSex;
        this.usercode = usercode;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean isSex() {
        return sex;
    }

    public void setSex(boolean sex) {
        this.sex = sex;
    }

    public int getUsercode() {
        return usercode;
    }

    public void setUsercode(int usercode) {
        this.usercode = usercode;
    }

    @Override
    public String getParams() {
        return "id=" + usercode + "&nickName=" + userName + "&sex=" + sex;
    }

    @Override
    public String parseData(String str) {
        return null;
    }

}

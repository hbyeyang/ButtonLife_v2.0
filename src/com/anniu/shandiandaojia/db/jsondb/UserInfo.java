package com.anniu.shandiandaojia.db.jsondb;

import java.io.Serializable;

/**
 * @author YY
 * @ClassName: UserInfo
 * @Description: 用户信息
 * @date 2015年6月3日 下午3:57:37
 */
public class UserInfo implements Serializable {
    private static final long serialVersionUID = -5202626219389858707L;
    private int id; // ID
    private String name;// 姓名
    private String nickName;// 昵称
    private String tel;// 电话
    private boolean sex; // 性别
    private String address;// 地址

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

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public boolean isSex() {
        return sex;
    }

    public void setSex(boolean sex) {
        this.sex = sex;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}

package com.anniu.shandiandaojia.db.jsondb;

import java.io.Serializable;

/***
 * @author zxl
 * @ClassName: Banner
 * @Description: 首页banner
 * @date 2015年6月2日 下午1:57:43
 */
public class Banner implements Serializable {

    private static final long serialVersionUID = 2507650635958172766L;

    private int id;
    private String no;
    private String title;
    private String type;
    private String pictureUrl;
    private String href;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }
}

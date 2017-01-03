package com.anniu.shandiandaojia.db.jsondb;

import java.io.Serializable;

/***
 * @author zxl
 * @ClassName: Banner
 * @Description: 分页
 * @date 2015年6月2日 下午1:57:43
 */
public class Paging implements Serializable {
    private int page;
    private int limit;
    private int itemCount;
    private int start;
    private int pageCount;

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

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }
}

package com.leyou.common.pojo;

import java.util.Map;

public class SearchRequest {

    private String key;

    private Integer page;

    private Integer size;

    private String sortBy;  //排序字段

    private Boolean descending;//是否降序

    private Map<String,String>  filter; //过滤条件

    private static  final  Integer DEFAULT_SIZE = 20;  //默认每页显示20条
    private static  final  Integer DEFAULT_PAGE = 1;   // 默认第一页


    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public Boolean getDescending() {
        return descending;
    }

    public void setDescending(Boolean descending) {
        this.descending = descending;
    }

    public Map<String, String> getFilter() {
        return filter;
    }

    public void setFilter(Map<String, String> filter) {
        this.filter = filter;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Integer getPage() {
        return this.page == null ? this.DEFAULT_PAGE:Math.max(this.DEFAULT_PAGE,this.page);
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return this.size == null ? this.DEFAULT_SIZE:Math.max(this.DEFAULT_SIZE,this.size);
    }

    public void setSize(Integer size) {
        this.size = size;
    }
}

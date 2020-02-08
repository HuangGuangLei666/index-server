package com.pl.indexserver.query;

import org.springframework.util.StringUtils;

public class TUnknownRecordQuery extends BaseQuery {

    private Integer pageSize;

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    @Override
    public int getPageNum() {
        if(!StringUtils.isEmpty(pageSize)){
            return pageSize;
        }else {
            return super.getPageNum();
        }
    }
}

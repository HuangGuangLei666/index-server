package com.pl.indexserver.model;

import java.util.Date;

public class CallTaskIndexDto {

    private Date endTime;

    private Long sumRecords;

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Long getSumRecords() {
        return sumRecords;
    }

    public void setSumRecords(Long sumRecords) {
        this.sumRecords = sumRecords;
    }
}

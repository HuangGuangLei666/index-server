package com.pl.indexserver.model;

public class TOrdDiskInfoDto {

    private long diskSpaceSize;
    private String expireTtime;

    public long getDiskSpaceSize() {
        return diskSpaceSize;
    }

    public void setDiskSpaceSize(long diskSpaceSize) {
        this.diskSpaceSize = diskSpaceSize;
    }

    public String getExpireTtime() {
        return expireTtime;
    }

    public void setExpireTtime(String expireTtime) {
        this.expireTtime = expireTtime;
    }
}

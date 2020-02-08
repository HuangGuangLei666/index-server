package com.pl.model;

import java.util.Objects;

public class TmCustomer {
    private Long id;

    private Long task_id;

    private String ctid;

    private String ctname;

    private Long companyId;

    private String ctAddress;

    private String ctSex;

    private String ctPosition;

    private String car_numbers; //车牌号

    private String ctPhone;

    private String ctType;

    private Integer cmType;


    public TmCustomer() {
    }

    public TmCustomer(String ctid, String ctname, Long companyId, String ctAddress, String ctSex, String ctPosition, String ctPhone, String ctType) {
        this.ctid = ctid;
        this.ctname = ctname;
        this.companyId = companyId;
        this.ctAddress = ctAddress;
        this.ctSex = ctSex;
        this.ctPosition = ctPosition;
        this.ctPhone = ctPhone;
        this.ctType = ctType;
    }

    public TmCustomer(String ctid, String ctname, Long companyId, String ctAddress, String ctSex, String ctPosition, String ctPhone, String ctType,Integer cmType) {
        this.ctid = ctid;
        this.ctname = ctname;
        this.companyId = companyId;
        this.ctAddress = ctAddress;
        this.ctSex = ctSex;
        this.ctPosition = ctPosition;
        this.ctPhone = ctPhone;
        this.ctType = ctType;
        this.cmType = cmType;
    }

    public String getCar_numbers() {
        return car_numbers;
    }

    public void setCar_numbers(String car_numbers) {
        this.car_numbers = car_numbers;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCtid() {
        return ctid;
    }

    public void setCtid(String ctid) {
        this.ctid = ctid == null ? null : ctid.trim();
    }

    public String getCtname() {
        return ctname;
    }

    public void setCtname(String ctname) {
        this.ctname = ctname == null ? null : ctname.trim();
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getCtAddress() {
        return ctAddress;
    }

    public void setCtAddress(String ctAddress) {
        this.ctAddress = ctAddress == null ? null : ctAddress.trim();
    }

    public String getCtSex() {
        return ctSex;
    }

    public void setCtSex(String ctSex) {
        this.ctSex = ctSex == null ? null : ctSex.trim();
    }

    public String getCtPosition() {
        return ctPosition;
    }

    public void setCtPosition(String ctPosition) {
        this.ctPosition = ctPosition == null ? null : ctPosition.trim();
    }

    public String getCtPhone() {
        return ctPhone;
    }

    public void setCtPhone(String ctPhone) {
        this.ctPhone = ctPhone == null ? null : ctPhone.trim();
    }

    public String getCtType() {
        return ctType;
    }

    public void setCtType(String ctType) {
        this.ctType = ctType == null ? null : ctType.trim();
    }

    public Long getTask_id() {
        return task_id;
    }

    public void setTask_id(Long task_id) {
        this.task_id = task_id;
    }

    public Integer getCmType() {
        return cmType;
    }

    public void setCmType(Integer cmType) {
        this.cmType = cmType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TmCustomer)) return false;
        TmCustomer customer = (TmCustomer) o;
//        System.out.println(Objects.equals(ctPhone, customer.ctPhone));
        return Objects.equals(ctPhone, customer.ctPhone);
    }

    @Override
    public int hashCode() {
        return ctPhone.hashCode();
    }

}
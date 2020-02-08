package com.pl.model;

import java.util.Date;

public class IntelligentJump {

    private Long id;

    private String name; 
 
    private String desc;

    private String status;

    private Date updatetime = new Date();
    
  
    private Long paramter;
    
    private Long times;
    private Long businessId;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Date getUpdatetime() {
		return updatetime;
	}
	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}
	public Long getParamter() {
		return paramter;
	}
	public void setParamter(Long paramter) {
		this.paramter = paramter;
	}
	public Long getTimes() {
		return times;
	}
	public void setTimes(Long times) {
		this.times = times;
	}
	public Long getBusinessId() {
		return businessId;
	}
	public void setBusinessId(Long businessId) {
		this.businessId = businessId;
	}
   

    
}
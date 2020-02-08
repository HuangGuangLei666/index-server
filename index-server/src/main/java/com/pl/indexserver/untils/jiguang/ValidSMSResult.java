package com.pl.indexserver.untils.jiguang;

import cn.jiguang.common.resp.BaseResult;
import com.google.gson.annotations.Expose;


public class ValidSMSResult extends BaseResult {

	@Expose
    Boolean isValid;
	@Expose
	int retCode;
	@Expose
	String retDesc;
	
	public Boolean getIsValid() {
		return isValid;
	}

	public Boolean getValid() {
		return isValid;
	}

	public void setValid(Boolean valid) {
		isValid = valid;
	}

	public int getRetCode() {
		return retCode;
	}

	public void setRetCode(int retCode) {
		this.retCode = retCode;
	}

	public String getRetDesc() {
		return retDesc;
	}

	public void setRetDesc(String retDesc) {
		this.retDesc = retDesc;
	}
}

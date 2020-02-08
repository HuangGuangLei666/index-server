package com.pl.indexserver.untils.jiguang;

import cn.jiguang.common.resp.BaseResult;
import com.google.gson.annotations.Expose;

public class SendSMSResult extends BaseResult {

	@Expose
	int retCode;
	@Expose
	String retDesc;


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

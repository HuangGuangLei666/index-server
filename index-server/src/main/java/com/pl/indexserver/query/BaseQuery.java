package com.pl.indexserver.query;

@SuppressWarnings("unused")
public class BaseQuery {

	private int pageNum = 10;// 每页显示数
	private int pageIndex = 1;// 当前页码

	private long begin;// 起始下标

	private boolean isLimit = true;// 是否开启分页

	private Long companyId;// 公司id

	// 通用查询条件(模糊搜索)
	private String name;

	public int getPageNum() {
		return pageNum;
	}

	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}

	public int getPageIndex() {
		return pageIndex;
	}

	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getBegin() {
		return (long) ((pageIndex - 1) * pageNum);
	}

	public void setBegin(Long begin) {
		this.begin = begin;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public boolean isLimit() {
		return isLimit;
	}

	public void setLimit(boolean isLimit) {
		this.isLimit = isLimit;
	}

}

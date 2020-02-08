package com.pl.indexserver.query;

import java.util.ArrayList;
import java.util.List;

public class Page<T> {

	private long total;// 总数
	private List<T> records = new ArrayList<>();// 查询到的数列表

	public Page() {
	}

	public Page(long total, List<T> records) {
		this.total = total;
		if (records != null && records.size() > 0) {
			this.records = records;
		}
	}

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public List<T> getRecords() {
		return records;
	}

	public void setRecords(List<T> records) {
		this.records = records;
	}

}

package com.ericsson.skt.model;

import java.util.HashMap;
import java.util.Map;

public class PageQueryCondition {
	private String offset = "";
	private String limit = "";
	private String sort = "";
	private long total = 0;
	private Map<String, Object> queryCondition = new HashMap<String, Object>();

	public PageQueryCondition(String offset, String limit, String sort) {
		if(offset != null)
			setOffset(offset);
		if(limit != null)
			setLimit(limit);
		if(sort != null)
			setSort(sort);
	}

	public PageQueryCondition(Map<String, Object> queryCondition) {
		this.setQueryCondition(queryCondition);
	}

	public PageQueryCondition() {
	}

	public String getOffset() {
		return offset;
	}

	public void setOffset(String offset) {
		this.offset = offset;
	}

	public String getLimit() {
		return limit;
	}

	public void setLimit(String limit) {
		this.limit = limit;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public void addQueryConditon(String key, Object value) {
		queryCondition.put(key, value);
	}
	
	public void addNotNullStringCondition(String key, String value) {
		if(value == null || value.equals(""))
			return;
		queryCondition.put(key, value);
	}

	public void addNotNullCondition(String key, Object value) {
		if(value != null)
			queryCondition.put(key, value);
	}

	public void addNotNullIntegerCondition(String key, String value) {
		if(value != null && !value.equals(""))
			queryCondition.put(key, Integer.valueOf(value));
	}

	public void removeQueryCondition(String key) {
		queryCondition.remove(key);
	}

	public Map<String, Object> getQueryCondition() {
		return queryCondition;
	}
	
	public void setQueryCondition(Map<String, Object> queryCondition) {
		this.queryCondition = queryCondition;
	}

	@Override
	public String toString() {
		return "PageQueryCondition [offset=" + offset + ",limit=" + limit
				+ ",sort=" + sort + ",total=" + total + ",queryCondition="
				+ queryCondition + "]";
	}
}

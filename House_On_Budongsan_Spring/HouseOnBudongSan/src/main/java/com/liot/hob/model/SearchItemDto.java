package com.liot.hob.model;

public class SearchItemDto {
	private long value;		//실제 값
	private String title;	//사용자에게 보여줄 값
	
	public long getValue() {
		return value;
	}
	public void setValue(long value) {
		this.value = value;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	@Override
	public String toString() {
		return String.format("%s[%d]", title, value);
	}
}

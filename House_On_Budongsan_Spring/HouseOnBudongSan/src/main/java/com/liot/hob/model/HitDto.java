package com.liot.hob.model;

public class HitDto {
	private String name;
	private Long code;
	private int count;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getCode() {
		return code;
	}
	public void setCode(Long code) {
		this.code = code;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	@Override
	public String toString() {
		return "HitDto [name=" + name + ", code=" + code + ", count=" + count + "]";
	}
}

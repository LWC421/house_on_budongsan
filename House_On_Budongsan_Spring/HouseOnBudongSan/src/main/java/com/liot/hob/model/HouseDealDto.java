package com.liot.hob.model;

public class HouseDealDto {
	private long houseDealCode;
	
	private int dealAmount;
	private int dealYear;
	private int dealMonth;
	private int dealDay;
	
	private double area;
	private int floor;

	private long houseInfoCode;

	public long getHouseDealCode() {
		return houseDealCode;
	}

	public void setHouseDealCode(long houseDealCode) {
		this.houseDealCode = houseDealCode;
	}

	public int getDealAmount() {
		return dealAmount;
	}

	public void setDealAmount(int dealAmount) {
		this.dealAmount = dealAmount;
	}

	public int getDealYear() {
		return dealYear;
	}

	public void setDealYear(int dealYear) {
		this.dealYear = dealYear;
	}

	public int getDealMonth() {
		return dealMonth;
	}

	public void setDealMonth(int dealMonth) {
		this.dealMonth = dealMonth;
	}

	public int getDealDay() {
		return dealDay;
	}

	public void setDealDay(int dealDay) {
		this.dealDay = dealDay;
	}
	
	public double getArea() {
		return area;
	}

	public void setArea(double area) {
		this.area = area;
	}

	public int getFloor() {
		return floor;
	}

	public void setFloor(int floor) {
		this.floor = floor;
	}

	public long getHouseInfoCode() {
		return houseInfoCode;
	}

	public void setHouseInfoCode(long houseInfoCode) {
		this.houseInfoCode = houseInfoCode;
	}

	@Override
	public String toString() {
		return "HouseDealDto [houseDealCode=" + houseDealCode + ", dealAmount=" + dealAmount + ", dealYear=" + dealYear
				+ ", dealMonth=" + dealMonth + ", dealDay=" + dealDay + ", houseInfoCode=" + houseInfoCode + "]";
	}
}

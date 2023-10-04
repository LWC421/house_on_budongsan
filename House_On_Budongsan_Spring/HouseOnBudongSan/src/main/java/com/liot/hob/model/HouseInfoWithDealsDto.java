package com.liot.hob.model;

import java.util.LinkedList;
import java.util.List;

public class HouseInfoWithDealsDto extends HouseInfoDto{
	private List<HouseDealDto> deals;
	
	public HouseInfoWithDealsDto() {
		this.deals = new LinkedList<>();
	}
	
	public void setHouseInfo(HouseInfoDto house) {
		this.setHouseInfoCode(house.getHouseInfoCode());
		this.setBuildYear(house.getBuildYear());

		this.setRoadName(house.getRoadName());
		this.setRoadNameBonbun(house.getRoadNameBonbun());
		this.setRoadNameSeq(house.getRoadNameSeq());
		this.setRoadNameBasementCode(house.getRoadNameBasementCode());
		this.setRoadNameCode(house.getRoadNameCode());
		this.setDong(house.getDong());
		this.setBonbun(house.getBonbun());
		this.setBubun(house.getBubun());
		this.setLandCode(house.getLandCode());
		this.setApartmentName(house.getApartmentName());
		this.setJibun(house.getJibun());

		this.setLng(house.getLng());
		this.setLat(house.getLat());

		this.setDongCode(house.getDongCode());
		this.setGugunCode(house.getGugunCode());
		this.setSidoCode(house.getSidoCode());
		
		this.setFullName(house.getFullName());
	}

	public List<HouseDealDto> getDeals() {
		return deals;
	}

	public void setDeals(List<HouseDealDto> deals) {
		this.deals = deals;
	}

	@Override
	public String toString() {
		return "HouseInfoWithDealsDto [deals=" + deals + ", toString()=" + super.toString() + "]";
	}
}

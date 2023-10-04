package com.liot.hob.model.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.liot.hob.model.HitDto;
import com.liot.hob.model.HouseDealDto;
import com.liot.hob.model.HouseInfoDto;
import com.liot.hob.model.SearchItemDto;

@Mapper
public interface HouseMapper {
	public List<SearchItemDto> sidoList(String keyword) throws Exception;
	public List<SearchItemDto> gugunList(String keyword) throws Exception;
	public List<SearchItemDto> dongList(String keyword) throws Exception;
	
	public List<SearchItemDto> buildingList(String keyword) throws Exception;
	
	public List<HouseInfoDto> houseInfoListBySidoCode(long sidoCode) throws Exception;
	public List<HouseInfoDto> houseInfoListByGugunCode(long gugunCode) throws Exception;
	public List<HouseInfoDto> houseInfoListByDongCode(long dongCode) throws Exception;
	
	public List<HouseDealDto> houseDealListBySidoCode(Map<String, Long> searchCondition) throws Exception;
	public List<HouseDealDto> houseDealListByGugunCode(Map<String, Long> searchCondition) throws Exception;
	public List<HouseDealDto> houseDealListByDongCode(Map<String, Long> searchCondition) throws Exception;
	
	public int sidoHit(Map<String, Long> searchCondition) throws Exception;
	public int gugunHit(Map<String, Long> searchCondition) throws Exception;
	public int dongHit(Map<String, Long> searchCondition) throws Exception;
	
	public List<HitDto> getRecentSidoHit() throws Exception;
	public List<HitDto> getRecentGugunHit() throws Exception;
	public List<HitDto> getRecentDongHit() throws Exception;
	public List<HitDto> getRecentBuildingLike() throws Exception;
	
	public List<HouseInfoDto> houseInfoListByBuildingCode(Long searchKeyword) throws Exception;
	public List<HouseDealDto> houseDealListByBuildingCode(Map<String, Long> condition) throws Exception;
	public List<HouseDealDto> allHouseDealListByBuildingCode(Long houseInfoCode) throws Exception;
	
	public List<Long> likeListByMemberCode(Long memberCode) throws Exception;
	public List<HouseInfoDto> likeListDetailByMemberCode(Long memberCode);
	public int addLike(Map<String, Long> body) throws Exception;
	public int removeLike(Map<String, Long> body) throws Exception;
}

package com.liot.hob.model.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.liot.hob.model.HitDto;
import com.liot.hob.model.HouseDealDto;
import com.liot.hob.model.HouseInfoDto;
import com.liot.hob.model.HouseInfoWithDealsDto;
import com.liot.hob.model.MemberDto;
import com.liot.hob.model.SearchItemDto;
import com.liot.hob.model.mapper.HouseMapper;

@Service
public class HouseService {
	
	private HouseMapper mapper;
	private MemberService memberService;
	
	@Value("${spring.datasource.url}")
	private String dbURL;
	
	@Value("${spring.datasource.hikari.username}")
	private String id;
	
	@Value("${spring.datasource.hikari.password}")
	private String password;

	@Autowired
	public HouseService(HouseMapper houseMapper, MemberService memberService) {
		this.mapper = houseMapper;
		this.memberService = memberService;
		
		//JDBC를 사용하기위해 설정
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		}
		catch(Exception e) {
			System.out.println("Driver Error");
			e.printStackTrace();
		}
	}
	
	//키워드에 대한 시군구동에 대한 정보 반환
	public List<SearchItemDto> locationList(String keyword) throws Exception {
		List<SearchItemDto> result = new LinkedList<>();
		result.addAll(mapper.sidoList(keyword));
		result.addAll(mapper.gugunList(keyword));
		result.addAll(mapper.dongList(keyword));
		return result;
	}
	
	//키워드에 대한 건물 정보 반환
	public List<SearchItemDto> buildingList(String keyword) throws Exception {
		List<SearchItemDto> result = mapper.buildingList(keyword);
		return result;
	}
	
	//하우스 정보와 거래정보를 가져와서 합쳐서 반환해준다
	public List<HouseInfoWithDealsDto> processingList(List<HouseInfoDto> houses, List<HouseDealDto> deals){
		List<HouseInfoWithDealsDto> resultTmp = new LinkedList<>();
		
		Map<Long, HouseInfoWithDealsDto> resultMap = new HashMap<>();
		
		//일단 자기 자신 넣고
		for(HouseInfoDto house: houses) {
			HouseInfoWithDealsDto houseDeal = new HouseInfoWithDealsDto();
			houseDeal.setHouseInfo(house);
			resultMap.put(house.getHouseInfoCode(), houseDeal);
			resultTmp.add(houseDeal);
		}
		
		//각각에 거래에 대해 처리해주자
		for(HouseDealDto deal: deals) {
			long houseInfoCode = deal.getHouseInfoCode();
			HouseInfoWithDealsDto target = resultMap.get(houseInfoCode);	//넣어야 되는 곳을 가져와서
			target.getDeals().add(deal);	//해당 거래를 넣어주자
		}
		
		//거래 정보가 없는 것들은 거르자
		List<HouseInfoWithDealsDto> result = new LinkedList<>();
		for(HouseInfoWithDealsDto tmp : resultTmp) {
			if(tmp.getDeals().size() != 0) {
				//해당하는 거래가 있을 때만
				result.add(tmp);
			}
		}
		
		return result;
	}
	
	//시군구동 또는 건물에 대해 정보 받아오기
	public List<HouseInfoWithDealsDto> dealList(String searchBy, Long searchKeyword, Long year, Long month) throws Exception {
		Map<String, Long> condition = new HashMap<>();
		condition.put("code", searchKeyword);
		condition.put("year", year);
		condition.put("month", month);
		
		List<HouseInfoDto> houses = null;
		List<HouseDealDto> deals = null;
		if(searchBy.equals("location")) {
			//장소 검색인 경우
			if(10 <= searchKeyword && searchKeyword <= 99) {
				//시도 검색인 경우
				houses = mapper.houseInfoListBySidoCode(searchKeyword);
				deals = mapper.houseDealListBySidoCode(condition);
			}
			else if(100 <= searchKeyword && searchKeyword <= 99999) {
				//구군 검색인 경우
				houses = mapper.houseInfoListByGugunCode(searchKeyword);
				deals = mapper.houseDealListByGugunCode(condition);
			}
			else {
				//동 검색인 경우
				houses = mapper.houseInfoListByDongCode(searchKeyword);
				deals = mapper.houseDealListByDongCode(condition);
			}
		}
		else if(searchBy.equals("building")) {
			//건물로 검색인 경우
			houses = mapper.houseInfoListByBuildingCode(searchKeyword);
			deals = mapper.houseDealListByBuildingCode(condition);
			
		}
		List<HouseInfoWithDealsDto> result = processingList(houses, deals);

		return result;
	}
	
	public void hit(String memberId, String searchBy, Long searchKeyword) throws Exception {
		Map<String, Long> body = new HashMap<>();
		MemberDto member = memberService.idCheck(memberId);
		Long memberCode = member.getMemberCode();
		
		body.put("memberCode", memberCode);
		body.put("code", searchKeyword);
		
		if(searchBy.equals("location")) {
			//장소 검색인 경우
			if(10 <= searchKeyword && searchKeyword <= 99) {
				//시도 검색인 경우
				mapper.sidoHit(body);
			}
			else if(100 <= searchKeyword && searchKeyword <= 99999) {
				//구군 검색인 경우
				mapper.gugunHit(body);
			}
			else {
				//동 검색인 경우
				mapper.dongHit(body);
			}
		}
	}
	
	public List<HitDto> getRecentSidoHit() throws Exception{
		List<HitDto> result = mapper.getRecentSidoHit();
		return result;
	}
	
	public List<HitDto> getRecentGugunHit() throws Exception{
		List<HitDto> result = mapper.getRecentGugunHit();
		return result;
	}
	
	public List<HitDto> getRecentDongHit() throws Exception{
		List<HitDto> result = mapper.getRecentDongHit();
		return result;
	}
	
	public List<HitDto> getRecentBuildingLike() throws Exception{
		List<HitDto> result = mapper.getRecentBuildingLike();
		return result;
	}
	

	//houseInfoCode로부터 모든 날짜의 거래정보 받아오기
	public HouseInfoWithDealsDto allDealListByHouseInfoCode(Long houseInfoCode) throws Exception{
		List<HouseInfoDto> houses = mapper.houseInfoListByBuildingCode(houseInfoCode);
		List<HouseDealDto> deals = mapper.allHouseDealListByBuildingCode(houseInfoCode);
		
		List<HouseInfoWithDealsDto> result = processingList(houses, deals);
		
		return result.get(0);
	}

	public List<Long> likeListByMemberCode(Long memberCode) throws Exception{
		List<Long> houses = mapper.likeListByMemberCode(memberCode);
		return houses;
	}
	

	public List<HouseInfoDto> likeListDetailByMemberCode(Long memberCode) {
		List<HouseInfoDto> houses = mapper.likeListDetailByMemberCode(memberCode);
		return houses;
	}

	public boolean addLike(Long memberCode, Long houseInfoCode) throws Exception{
		Map<String, Long> body = new HashMap<>();
		body.put("memberCode", memberCode);
		body.put("houseInfoCode", houseInfoCode);
		
		int result = mapper.addLike(body);
		return result == 1 ? true : false;
	}
	public boolean removeLike(Long memberCode, Long houseInfoCode) throws Exception{
		Map<String, Long> body = new HashMap<>();
		body.put("memberCode", memberCode);
		body.put("houseInfoCode", houseInfoCode);
		
		int result = mapper.removeLike(body);
		return result == 1 ? true : false;
	}

	//좌표로부터 거래정보를 받아옴
	public List<HouseInfoWithDealsDto> dealListByLatLng(Integer level, Double paramLat, Double paramLng, Long year, Long month) throws Exception{
		int meter = 0;
		
		if(level <= 5) {
			//동 수준의 검색이면
			meter = 1500;	//1.5km
		}
		else if(level >= 6 && level <= 8) {
			//군구 수준 검색
			meter = 4000;	//4km
		}
		else if(level >= 9) {
			//시 수준 검색
			meter = 20_000;	//20km
		}
		
		List<HouseInfoDto> houses = new LinkedList<>();
		List<HouseDealDto> deals = new LinkedList<>();
		Set<Long> houseSet = new HashSet<>();	//저장된건지 아닌건지 확인용

		Connection conn = DriverManager.getConnection(
				dbURL, 
				id, 
				password);
		
		//SQL문 정의
		StringBuilder sql = new StringBuilder("");
		sql.append("SELECT *, concat(s.sidoName, \' \', g.gugunName, \' \', d.dongName, \' \', h.jibun) fullName").append("\n");
		sql.append("FROM houseInfos h").append("\n");
		sql.append("JOIN houseDeals deal on h.houseInfoCode = deal.houseInfoCode").append("\n");
		sql.append("JOIN sidos s on s.sidoCode = h.sidoCode").append("\n");
		sql.append("JOIN guguns g on g.gugunCode = h.gugunCode").append("\n");
		sql.append("JOIN dongs d on d.dongcode = h.dongcode").append("\n");
		sql.append("WHERE ST_Distance_Sphere(point, point(?, ?)) <= ?").append("\n");
		sql.append("AND dealYear = ?").append("\n");
		sql.append("AND dealMonth = ?");
		
		PreparedStatement pstmt = null;
		ResultSet rst = null;
		
		try {
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setDouble(1, paramLng);
			pstmt.setDouble(2, paramLat);
			pstmt.setInt(3, meter);
			pstmt.setLong(4, year);
			pstmt.setLong(5, month);
			
			rst = pstmt.executeQuery();
			while(rst.next()) {
				long houseInfoCode = rst.getLong("houseInfoCode");
				Integer buildYear = rst.getInt("buildYear");
				String roadName = rst.getString("roadName");
				String roadNameBonbun = rst.getString("roadNameBonbun");
				String roadNameSeq = rst.getString("roadNameSeq");
				String roadNameBasementCode = rst.getString("roadNameBasementCode");
				String roadNameCode = rst.getString("roadNameCode");
				String dong = rst.getString("dong");
				String bonbun = rst.getString("bonbun");
				String bubun = rst.getString("bubun");
				String landCode = rst.getString("landCode");
				String apartmentName = rst.getString("apartmentName");
				String jibun = rst.getString("jibun");
				Double lng = rst.getDouble("lng");
				Double lat = rst.getDouble("lat");
				Long dongCode = rst.getLong("dongCode");
				Long gugunCode = rst.getLong("gugunCode");
				Long sidoCode = rst.getLong("sidoCode");
				Long houseDealCode = rst.getLong("houseDealCode");
				Integer dealAmount = rst.getInt("dealAmount");
				Integer dealYear = rst.getInt("dealYear");
				Integer dealMonth = rst.getInt("dealMonth");
				Integer dealDay = rst.getInt("dealDay");
				Double area = rst.getDouble("area");
				Integer floor = rst.getInt("floor");
				String fullName = rst.getString("fullName");

				if( !houseSet.contains(houseInfoCode) ) {
					//추가가 안된 집일때만
					
					//하우스 객체 만들기
					HouseInfoDto house = new HouseInfoDto();
					house.setHouseInfoCode(houseInfoCode);
					house.setBuildYear(buildYear);
					
					house.setRoadName(roadName);
					house.setRoadNameBonbun(roadNameBonbun);
					house.setRoadNameSeq(roadNameSeq);
					house.setRoadNameBasementCode(roadNameBasementCode);
					house.setRoadNameCode(roadNameCode);
					house.setDong(dong);
					house.setBonbun(bonbun);
					house.setBubun(bubun);
					house.setLandCode(landCode);
					house.setApartmentName(apartmentName);
					house.setJibun(jibun);

					house.setLng(lng);
					house.setLat(lat);
					
					house.setDongCode(dongCode);
					house.setGugunCode(gugunCode);
					house.setSidoCode(sidoCode);
					
					house.setFullName(fullName);
					
					houses.add(house);				//집객체 넣고
					houseSet.add(houseInfoCode);	//집객체 넣었다고 넣기
				}
				
				//거래객체 생성하기
				HouseDealDto deal = new HouseDealDto();
				deal.setHouseDealCode(houseDealCode);
				deal.setDealAmount(dealAmount);
				deal.setDealYear(dealYear);
				deal.setDealMonth(dealMonth);
				deal.setDealDay(dealDay);
				
				deal.setArea(area);
				deal.setFloor(floor);
				
				deal.setHouseInfoCode(houseInfoCode);
				
				deals.add(deal);	//거래객체 넣기
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally {
			if(rst != null) {
				try {
					rst.close();
				}
				catch(Exception e) {
					System.out.println("Result Set Close Error");
				}
			}
			if(pstmt != null) {
				try {
					pstmt.close();
				}
				catch(Exception e) {
					System.out.println("Prepared Statement Close Error");
				}
			}
			if(conn != null) {
				try {
					conn.close();
				}
				catch(Exception e) {
					System.out.println("Connection Close Error");
				}
			}
		}
		
		//집객체와 거래객체 합치기
		List<HouseInfoWithDealsDto> result = processingList(houses, deals);
		return result;
	}


}

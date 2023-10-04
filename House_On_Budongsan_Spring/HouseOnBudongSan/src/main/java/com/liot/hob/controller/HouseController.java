package com.liot.hob.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.liot.hob.model.HitDto;
import com.liot.hob.model.HouseInfoDto;
import com.liot.hob.model.HouseInfoWithDealsDto;
import com.liot.hob.model.SearchItemDto;
import com.liot.hob.model.service.HouseService;
import com.liot.hob.model.service.JwtServiceImpl;

@RestController
@RequestMapping("/house")
public class HouseController {
	
	private HouseService service;
	private JwtServiceImpl jwtService;
	
	@Autowired
	public HouseController(HouseService service, JwtServiceImpl jwtService) {
		this.service = service;
		this.jwtService = jwtService;
	}
	
	//지역들을 반환
	@GetMapping("/search/location/{keyword}")
	public ResponseEntity<?> locationListByKeyword(@PathVariable("keyword") String keyword) {
		try {
			List<SearchItemDto> result = service.locationList(keyword);
			return new ResponseEntity<List<SearchItemDto>>(result, HttpStatus.OK);
		}
		catch(Exception e) {
			return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
		}
	}
	
	//건물들을 반환
	@GetMapping("/search/building/{keyword}")
	public ResponseEntity<?> buildingListByKeyword(@PathVariable("keyword") String keyword) {
		try {
			List<SearchItemDto> result = service.buildingList(keyword);
			return new ResponseEntity<List<SearchItemDto>>(result, HttpStatus.OK);
		}
		catch(Exception e) {
			return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
		}
	}
	
	//거래정보 반환
	@GetMapping("/deal")
	public ResponseEntity<?> dealList(
			@RequestParam("searchBy") String searchBy,
			@RequestParam("searchKeyword") Long searchKeyword,
			@RequestParam("year") Long year,
			@RequestParam("month") Long month, 
			HttpServletRequest request) {
		
		if(searchBy == null || searchKeyword == null ||
				year == null || month == null) {
			return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
		}
		
		
		//만약 로그인 된 사용자가 검색을 요청한 경우
		// -> 시군구동 및 건물에 대한 로그를 넣어주자
		if(jwtService.checkToken(request.getHeader("access-token"))) {
			Map<String, Object> info = jwtService.get(request.getHeader("access-token"));
			String memberId = (String)info.get("memberId");
			try {
				service.hit(memberId, searchBy, searchKeyword);
			}
			catch(Exception e) {
			}	//에러가 나도 그냥 넘어가도록 하자
		}
		
		
		try {
			List<HouseInfoWithDealsDto> result = service.dealList(searchBy, searchKeyword, year, month);
			
			return new ResponseEntity<List<HouseInfoWithDealsDto>>(result, HttpStatus.OK);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
		}
	}
	
	//건물에 대한 모든 거래 정보 반환
	@GetMapping("/deal/{houseInfoCode}")
	public ResponseEntity<?> allDealListByHouseInfoCode(@PathVariable("houseInfoCode") Long houseInfoCode){
		if(houseInfoCode == null) {
			return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
		}
		else {
			try {
				HouseInfoWithDealsDto result = service.allDealListByHouseInfoCode(houseInfoCode);
				
				return new ResponseEntity<HouseInfoWithDealsDto>(result, HttpStatus.OK);
			} catch (Exception e) {
				return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
			}
		}
	}
	
	//좋아요 누른 건물 정보 받아오기
	@GetMapping("/like/{memberCode}")
	public ResponseEntity<?> likeList(
			@PathVariable("memberCode") Long memberCode,
			HttpServletRequest request) {
		if(jwtService.checkToken(request.getHeader("access-token"))) {
			try {
				List<Long> likes = service.likeListByMemberCode(memberCode);
				return new ResponseEntity<List<Long>>(likes, HttpStatus.OK);
			}
			catch(Exception e) {
				return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
			}
		}
		else {
			return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
		}
	}
	
	//좋아요 누른 건물 상세 정보 받아오기
	@GetMapping("/like/detail/{memberCode}")
	public ResponseEntity<?> likeDetailList(@PathVariable("memberCode") Long memberCode,
			HttpServletRequest request){
		if(jwtService.checkToken(request.getHeader("access-token"))) {
			try {
				List<HouseInfoDto> houses = service.likeListDetailByMemberCode(memberCode);
				return new ResponseEntity<List<HouseInfoDto>>(houses, HttpStatus.OK);
			}
			catch(Exception e) {
				return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
			}
		}
		else {
			return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
		}
	}
	
	
	//건물에 대한 좋아요 추가
	@PostMapping("/like/add/{memberCode}")
	public ResponseEntity<?> addLike(
			@PathVariable("memberCode") Long memberCode,
			HttpServletRequest request,
			@RequestBody Map<String, Long> body) {
		if(jwtService.checkToken(request.getHeader("access-token"))) {
			try {
				Long houseInfoCode = body.get("houseInfoCode");
				service.addLike(memberCode, houseInfoCode);
				return new ResponseEntity<String>("add like", HttpStatus.OK);
			}
			catch(Exception e) {
				return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
			}
		}
		else {
			return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
		}
	}
	
	//건물에 대한 좋아요 취소
	@PostMapping("/like/remove/{memberCode}")
	public ResponseEntity<?> removeLike(
			@PathVariable("memberCode") Long memberCode,
			HttpServletRequest request,
			@RequestBody Map<String, Long> body) {
		if(jwtService.checkToken(request.getHeader("access-token"))) {
			try {
				Long houseInfoCode = body.get("houseInfoCode");
				service.removeLike(memberCode, houseInfoCode);
				return new ResponseEntity<String>("add like", HttpStatus.OK);
			}
			catch(Exception e) {
				return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
			}
		}
		else {
			return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
		}
	}
	
	//거래정보 반환
	@GetMapping("/dealbylatlng")
	public ResponseEntity<?> dealListByLatLng(
			@RequestParam("level") Integer level,
			@RequestParam("lat") Double lat,
			@RequestParam("lng") Double lng,
			@RequestParam("year") Long year,
			@RequestParam("month") Long month) {
		
		if(level == null || lat == null || lng == null ||
				year == null || month == null) {
			return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
		}
		
		try {
			List<HouseInfoWithDealsDto> result = service.dealListByLatLng(level, lat, lng, year, month);
			
			return new ResponseEntity<List<HouseInfoWithDealsDto>>(result, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
		}
	}
	
	@GetMapping("/rank")
	public ResponseEntity<?> recentHit(@RequestParam("searchBy") String searchBy){
		if(searchBy == null) {
			return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
		}

		if(searchBy.equals("sido")) {
			//시도의 정보를 원하는 거면
			try {
				List<HitDto> result = service.getRecentSidoHit();
				return new ResponseEntity<List<HitDto>>(result, HttpStatus.OK);
			}
			catch(Exception e) {
				return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
			}
		}
		else if(searchBy.equals("gugun")) {
			//구군의 정보를 원하는 거면
			try {
				List<HitDto> result = service.getRecentGugunHit();
				return new ResponseEntity<List<HitDto>>(result, HttpStatus.OK);
			}
			catch(Exception e) {
				return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
			}
		}
		else if(searchBy.equals("dong")) {
			//동의 정보를 원하는 거면
			try {
				List<HitDto> result = service.getRecentDongHit();
				return new ResponseEntity<List<HitDto>>(result, HttpStatus.OK);
			}
			catch(Exception e) {
				return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
			}
		}
		else if(searchBy.equals("building")){
			//건물의 정보를 원하는 거면
			try {
				List<HitDto> result = service.getRecentBuildingLike();
				return new ResponseEntity<List<HitDto>>(result, HttpStatus.OK);
			}
			catch(Exception e) {
				return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
			}
		}
		else {
			//잘못 된 요청이다
			return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
		}
	}
}

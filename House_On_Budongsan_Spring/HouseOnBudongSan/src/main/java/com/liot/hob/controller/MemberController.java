package com.liot.hob.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.liot.hob.model.MemberDto;
import com.liot.hob.model.ResponseCode;
import com.liot.hob.model.service.JwtServiceImpl;
import com.liot.hob.model.service.MemberService;

@CrossOrigin("*")
@RestController
@RequestMapping("/member")
public class MemberController {
	
	public static final Logger logger = LoggerFactory.getLogger(MemberController.class);
	private final MemberService memberService;
	private final JwtServiceImpl jwtService;
	
	@Autowired
	public MemberController(MemberService memberService, JwtServiceImpl jwtService) {
		this.memberService = memberService;
		this.jwtService = jwtService;
	}
	
	/**
	 * 
	 * @param memberDto
	 * @return {@code ResponseEntity<ResponseCode>}
	 * @throws Exception
	 */
	@PostMapping(value="/login")
	public ResponseEntity<?> login(@RequestBody MemberDto memberDto) throws Exception{
		Map<String, Object> resultMap = new HashMap<>();
		ResponseCode responseCode;
		try {
			MemberDto loginMember = memberService.login(memberDto);
			// 해당하는 아이디랑 비밀번호가 있음
			if(loginMember != null) {
				String accessToken = jwtService.createAccessToken("memberId", loginMember.getMemberId());
				String refreshToken = jwtService.createRefreshToken("memberId", loginMember.getMemberId());
				memberService.saveAccessToken(loginMember.getMemberId(), accessToken);
				memberService.saveRefreshToken(loginMember.getMemberId(), refreshToken);
				resultMap.put("access-token", accessToken);
				resultMap.put("refresh-token", refreshToken);
				responseCode = new ResponseCode.builder<Map<String, Object>>().code("SUCCESS").items(resultMap).build();
				return new ResponseEntity<ResponseCode>(responseCode, HttpStatus.OK);
			}
			// 해당하는 아이디랑 비밀번호가 없음
			else {
				responseCode = new ResponseCode.builder().code("FAIL").build();
				return new ResponseEntity<ResponseCode>(responseCode, HttpStatus.OK);
			} 		
		}catch(Exception e) {
			logger.error("로그인 실패: {}", e);
			responseCode = new ResponseCode.builder().code(e.getMessage()).build();
		}
		return new ResponseEntity<ResponseCode>(responseCode, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@GetMapping("/info/{memberId}")
	public ResponseEntity<?> getInfo(
			@PathVariable("memberId") String memberId,
			HttpServletRequest request) {
		logger.debug("memberId : {} ", memberId);
		Map<String, Object> resultMap = new HashMap<>();
		HttpStatus status = HttpStatus.UNAUTHORIZED;
		ResponseCode responseCode;
		if (jwtService.checkToken(request.getHeader("access-token"))) {
			logger.info("사용 가능한 토큰!!!");
			try {
//				로그인 사용자 정보.
				MemberDto memberDto = memberService.memberInfo(memberId);
				resultMap.put("memberInfo", memberDto);
				responseCode = new ResponseCode.builder<Map<String, Object>>().code("SUCCESS").items(resultMap).build();
				status = HttpStatus.OK;
			} catch (Exception e) {
				logger.error("정보조회 실패 : {}", e);
				responseCode = new ResponseCode.builder().code("FAIL").build();
				status = HttpStatus.OK;
			}
		} else {
			logger.error("사용 불가능 토큰!!!");
			responseCode = new ResponseCode.builder().code("TOKEN_FAIL").build();
			status = HttpStatus.OK;
		}
		return new ResponseEntity<ResponseCode>(responseCode, status);
	}
	
	@GetMapping("/logout/{memberId}")
	public ResponseEntity<?> removeToken(@PathVariable("memberId") String memberId) {
		Map<String, Object> resultMap = new HashMap<>();
		ResponseCode responseCode;
		HttpStatus status = HttpStatus.OK;
		
		try {
			memberService.deleteRefreshToken(memberId);
			responseCode = new ResponseCode.builder().code("SUCCESS").build();
			status = HttpStatus.OK;
		} catch (Exception e) {
			logger.error("로그아웃 실패 : {}", e);
			responseCode = new ResponseCode.builder().code(e.getMessage()).build();
			status = HttpStatus.INTERNAL_SERVER_ERROR;
		}
		return new ResponseEntity<ResponseCode>(responseCode, status);
	}
	
	@PostMapping("/refresh")
	public ResponseEntity<?> refreshToken(@RequestBody MemberDto memberDto, HttpServletRequest request)
			throws Exception {
		Map<String, Object> resultMap = new HashMap<>();
		ResponseCode responseCode = new ResponseCode.builder().code("SUCCESS").build();
		HttpStatus status = HttpStatus.OK;
		String token = request.getHeader("refresh-token");
		logger.debug("token : {}, memberDto : {}", token, memberDto);
		if (jwtService.checkToken(token)) {
			if (token.equals(memberService.getRefreshToken(memberDto.getMemberId()))) {
				String accessToken = jwtService.createAccessToken("memberId", memberDto.getMemberId());
				logger.debug("token : {}", accessToken);
				logger.debug("정상적으로 액세스토큰 재발급!!!");
				resultMap.put("access-token", accessToken);
				responseCode = new ResponseCode.builder<Map<String, Object>>().code("SUCCESS").items(resultMap).build();
				status = HttpStatus.OK;
			}
		} else {
			logger.debug("리프레쉬토큰도 사용불가!!!!!!!");
			responseCode = new ResponseCode.builder().code("FAIL").build();
			status = HttpStatus.OK;
		}
		return new ResponseEntity<ResponseCode>(responseCode, status);
	}
	
	/**
	 * 회원가입
	 * @param memberDto
	 * @return {@code ResponseEntity<ResponseCode>}
	 * @throws Exception
	 */
	@PostMapping
	public ResponseEntity<?> join(@RequestBody MemberDto memberDto) throws Exception{
		logger.info("join함수들어옴");
		boolean result = memberService.join(memberDto);
		
		// 성공
		if(result) {
			ResponseCode responseCode = new ResponseCode.builder().code("SUCCESS").build();
			return new ResponseEntity<ResponseCode>(responseCode, HttpStatus.OK);
		}
		// 실패
		else {
			ResponseCode responseCode = new ResponseCode.builder().code("FAIL").build();
			return new ResponseEntity<ResponseCode>(responseCode, HttpStatus.OK);
		}
	}
	
	@PostMapping("/modify")
	public ResponseEntity<?> modify(@RequestBody MemberDto memberDto) throws Exception{
		logger.info("modify함수들어옴");
		boolean result = memberService.modify(memberDto);
		
		// 성공
		if(result) {
			ResponseCode responseCode = new ResponseCode.builder().code("SUCCESS").build();
			return new ResponseEntity<ResponseCode>(responseCode, HttpStatus.OK);
		}
		// 실패
		else {
			ResponseCode responseCode = new ResponseCode.builder().code("FAIL").build();
			return new ResponseEntity<ResponseCode>(responseCode, HttpStatus.OK);
		}
	}
	
	/**
	 * 아이디 중복체크
	 * @param memberId
	 * @return {@code ResponseEntity<ResponseCode>}
	 */
	@PostMapping(value="/idCheck")
	public ResponseEntity<?> idCheck(@RequestBody MemberDto memberDto) throws Exception{
		
		MemberDto checkMember = memberService.idCheck(memberDto.getMemberId());
		ResponseCode responseCode;
		logger.info("memberId:{}", memberDto.getMemberId());
		logger.info("memberDto:{}", memberDto);
		
		// 아이디 일치하는 사람이 없다는 소리
		if(checkMember == null) {
			responseCode = new ResponseCode.builder().code("SUCCESS").build();			
			return new ResponseEntity<ResponseCode>(responseCode, HttpStatus.OK);
		}
		// 아이디 일치하는 사람이 있다는 소리
		else {
			responseCode = new ResponseCode.builder().code("FAIL").build();
			return new ResponseEntity<ResponseCode>(responseCode, HttpStatus.OK);
		}
	}
	
	/**
	 * 닉네임 중복체크
	 * @param nickname
	 * @return {@code ResponseEntity<ResponseCode>}
	 */
	@PostMapping(value="/nicknameCheck")
	public ResponseEntity<?> nicknameCheck(@RequestBody MemberDto memberDto)throws Exception{
		
		MemberDto checkMember = memberService.nicknameCheck(memberDto.getNickname());
		ResponseCode responseCode;

		// 아이디 일치하는 사람이 없다는 소리
		if(checkMember == null) {
			responseCode = new ResponseCode.builder().code("SUCCESS").build();			
			return new ResponseEntity<ResponseCode>(responseCode, HttpStatus.OK);
		}
		// 아이디 일치하는 사람이 있다는 소리
		else {
			responseCode = new ResponseCode.builder().code("FAIL").build();
			return new ResponseEntity<ResponseCode>(responseCode, HttpStatus.OK);
		}
	}
	
	@DeleteMapping("/{memberId}")
	public ResponseEntity<?> deleteMember(@PathVariable("memberId") String memberId) throws Exception{
		
		memberService.deleteMember(memberId);
		ResponseCode responseCode;

		responseCode = new ResponseCode.builder().code("SUCCESS").build();
		return new ResponseEntity<ResponseCode>(responseCode, HttpStatus.OK);
	}
	
	
}

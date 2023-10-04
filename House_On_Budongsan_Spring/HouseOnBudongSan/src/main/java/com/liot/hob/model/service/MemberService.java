package com.liot.hob.model.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.liot.hob.model.MemberDto;
import com.liot.hob.model.mapper.MemberMapper;

@Service
public class MemberService {
	
	private final MemberMapper memberMapper;
	
	@Autowired
	public MemberService(MemberMapper memberMapper) {
		this.memberMapper = memberMapper;
	}
	
	public MemberDto login(MemberDto memberDto) throws Exception{
		MemberDto loginMember = memberMapper.login(memberDto);
		
		return loginMember;
	}
	
	public void saveAccessToken(String memberId, String accessToken) throws Exception{
		Map<String, String> map = new HashMap<String, String>();
		map.put("memberId", memberId);
		map.put("accessToken", accessToken);
		memberMapper.saveAccessToken(map);
	}
	
	public void saveRefreshToken(String memberId, String refreshToken) throws Exception{
		Map<String, String> map = new HashMap<String, String>();
		map.put("memberId", memberId);
		map.put("refreshToken", refreshToken);
		memberMapper.saveRefreshToken(map);
	}
	
	public MemberDto memberInfo(String memberId) throws Exception{
		return memberMapper.memberInfo(memberId);
	}
	
	public Object getRefreshToken(String memberId) throws Exception{
		return memberMapper.getRefreshToken(memberId);
	}
	
	public void deleteRefreshToken(String memberId) throws Exception{
		Map<String, String> map = new HashMap<>();
		map.put("memberId", memberId);
		map.put("accessToken", null);
		map.put("refreshToken", null);
		memberMapper.deleteRefreshToken(map);	
	}
	
	/**
	 * 회원가입
	 * @param memberDto
	 * @return boolean
	 */
	@Transactional
	public boolean join(MemberDto memberDto) throws Exception{
		
		// db members table에 데이터 넣기
		Map<String, String> member = new HashMap<String, String>();
		member.put("memberId", memberDto.getMemberId());
		member.put("nickname", memberDto.getNickname());
		member.put("password", memberDto.getPassword());

		boolean memberResult = memberMapper.joinMember(member);
		
		// db memberinfos table에 데이터 넣기
		Map<String, String> memberinfo = new HashMap<String, String>();
		memberinfo.put("birthday", memberDto.getBirthday());
		memberinfo.put("email", memberDto.getEmail());
		boolean memberInfoResult = memberMapper.joinMemberInfo(memberinfo);
		
		return memberResult && memberInfoResult;
	}
	
	public boolean modify(MemberDto memberDto) throws Exception{
		
		// db members table에 데이터 넣기
		Map<String, String> member = new HashMap<String, String>();
		member.put("memberCode", String.valueOf(memberDto.getMemberCode()));
		member.put("nickname", memberDto.getNickname());
		member.put("password", memberDto.getPassword());

		boolean memberResult = memberMapper.modifyMember(member);
		
		// db memberinfos table에 데이터 넣기
		Map<String, String> memberinfo = new HashMap<String, String>();
		memberinfo.put("memberCode", String.valueOf(memberDto.getMemberCode()));
		memberinfo.put("email", memberDto.getEmail());
		memberinfo.put("birthday", memberDto.getBirthday());
		boolean memberInfoResult = memberMapper.modifyMemberInfo(memberinfo);
		
		return memberResult && memberInfoResult;
	}
	
	public void deleteMember(String memberId) throws Exception{
		memberMapper.deleteMember(memberId);
	}
	
	
	public MemberDto idCheck(String memberId) throws Exception{
		return memberMapper.idCheck(memberId);
	}
	
	public MemberDto nicknameCheck(String nickname) throws Exception{
		return memberMapper.nicknameCheck(nickname);
	}
	
	
	
}

package com.liot.hob.model.mapper;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.liot.hob.model.MemberDto;

@Mapper
public interface MemberMapper {
	MemberDto login(MemberDto memberDto) throws Exception;
	boolean joinMember(Map<String, String> member) throws Exception;
	boolean joinMemberInfo(Map<String, String> memberinfo) throws Exception;
	boolean modifyMember(Map<String, String> member) throws Exception;
	boolean modifyMemberInfo(Map<String, String> memberinfo) throws Exception;
	MemberDto idCheck(String memberId) throws Exception;
	MemberDto nicknameCheck(String nickname) throws Exception;
	void deleteMember(String memberId) throws Exception;
	
	void saveAccessToken(Map<String, String> map) throws Exception;
	void saveRefreshToken(Map<String, String> map) throws Exception;
	MemberDto memberInfo(String memberId) throws Exception;
	Object getRefreshToken(String memberId) throws Exception;
	void deleteRefreshToken(Map<String, String> map) throws Exception;
}

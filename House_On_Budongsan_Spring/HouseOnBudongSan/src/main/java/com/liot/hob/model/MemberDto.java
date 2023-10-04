package com.liot.hob.model;

public class MemberDto {
	private long memberCode;
	private String memberId;
	private String nickname;
	private String password;
	private String birthday;
	private String email;
	private String createdAt;
	private String deletedAt;
	private String updatedAt;
	
	public long getMemberCode() {
		return memberCode;
	}
	public void setMemberCode(long memberCode) {
		this.memberCode = memberCode;
	}
	public String getMemberId() {
		return memberId;
	}
	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getBirthday() {
		return birthday;
	}
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}
	public String getDeletedAt() {
		return deletedAt;
	}
	public void setDeletedAt(String deletedAt) {
		this.deletedAt = deletedAt;
	}
	public String getUpdatedAt() {
		return updatedAt;
	}
	public void setUpdatedAt(String updatedAt) {
		this.updatedAt = updatedAt;
	}

	@Override
	public String toString() {
		return "MemberDto [memberCode=" + memberCode + ", memberId=" + memberId + ", nickname=" + nickname
				+ ", password=" + password + ", birthday=" + birthday + ", email=" + email + ", createdAt=" + createdAt
				+ ", deletedAt=" + deletedAt + ", updatedAt=" + updatedAt + "]";
	}
	
}

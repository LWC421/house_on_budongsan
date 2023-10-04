package com.liot.hob.model;

public class BoardDto {
	private long boardCode;
	private String title;
	private String content;
	private int hit;
	private String createdAt;
	private String updatedAt;
	private String deletedAt;
	private long memberCode;
	public long getBoardCode() {
		return boardCode;
	}
	public void setBoardCode(long boardCode) {
		this.boardCode = boardCode;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getHit() {
		return hit;
	}
	public void setHit(int hit) {
		this.hit = hit;
	}
	public String getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}
	public String getUpdatedAt() {
		return updatedAt;
	}
	public void setUpdatedAt(String updatedAt) {
		this.updatedAt = updatedAt;
	}
	public String getDeletedAt() {
		return deletedAt;
	}
	public void setDeletedAt(String deletedAt) {
		this.deletedAt = deletedAt;
	}
	public long getMemberCode() {
		return memberCode;
	}
	public void setMemberCode(long memberCode) {
		this.memberCode = memberCode;
	}
	@Override
	public String toString() {
		return "BoardDto [boardCode=" + boardCode + ", title=" + title + ", content=" + content + ", hit=" + hit
				+ ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + ", deletedAt=" + deletedAt + ", memberCode="
				+ memberCode + "]";
	}
	
}

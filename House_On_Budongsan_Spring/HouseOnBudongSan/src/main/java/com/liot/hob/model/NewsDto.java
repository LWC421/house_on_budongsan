package com.liot.hob.model;

public class NewsDto {
	private long newsCode;
	private String title;
	private String description;
	private String originalLink;
	private String link;
	private String publishDate;
	public long getNewsCode() {
		return newsCode;
	}
	public void setNewsCode(long newsCode) {
		this.newsCode = newsCode;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getOriginalLink() {
		return originalLink;
	}
	public void setOriginalLink(String originalLink) {
		this.originalLink = originalLink;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getPublishDate() {
		return publishDate;
	}
	public void setPublishDate(String publishDate) {
		this.publishDate = publishDate;
	}
	@Override
	public String toString() {
		return "NewsDto [newsCode=" + newsCode + ", title=" + title + ", description=" + description + ", originalLink="
				+ originalLink + ", link=" + link + ", publishDate=" + publishDate + "]";
	}
}

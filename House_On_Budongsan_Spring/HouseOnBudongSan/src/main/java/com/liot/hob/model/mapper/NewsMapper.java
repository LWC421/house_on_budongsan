package com.liot.hob.model.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.liot.hob.model.NewsDto;

@Mapper
public interface NewsMapper {

	public NewsDto recentOne() throws Exception;
	public List<NewsDto> recentFive() throws Exception;
	public int insertMultiNews(List<NewsDto> newNews) throws Exception;
	public List<NewsDto> newsList() throws Exception;
	public List<NewsDto> getPageNews(int pageNo) throws Exception;
	public long getCount() throws Exception; 
}

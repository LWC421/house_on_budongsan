package com.liot.hob.model.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.liot.hob.model.BoardDto;

@Mapper
public interface BoardMapper {
	List<BoardDto> listArticle() throws Exception;
	List<Map<String, Object>> getPageArticle(int pageNo) throws Exception;
	List<Map<String, Object>> getHits(long boardCode) throws Exception;
	BoardDto getArticle(long boardCode) throws Exception;
	void updateHit(BoardDto boardDto) throws Exception;
	long getCount() throws Exception;
	List<BoardDto> recentFive() throws Exception;
	
	boolean writeArticle(BoardDto boardDto) throws Exception;
	boolean modifyArticle(BoardDto boardDto) throws Exception;
	boolean deleteArticle(long boardCode) throws Exception;
}

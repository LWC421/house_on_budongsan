package com.liot.hob.model.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.liot.hob.model.BoardDto;
import com.liot.hob.model.mapper.BoardMapper;

@Service
public class BoardService {
	
	private final BoardMapper boardMapper;
	
	@Autowired
	public BoardService(BoardMapper boardMapper) {
		this.boardMapper = boardMapper;
	}
	
	public List<BoardDto> listArticle() throws Exception{
		return boardMapper.listArticle();
	}
	
	public List<Map<String, Object>> getPageArticle(int pageNo) throws Exception{
		int calcPageNo = (pageNo-1) * 10;
		
		List<Map<String, Object>> boards = boardMapper.getPageArticle(calcPageNo);
		
		for(Map<String, Object> board: boards) {
			LocalDateTime localdateTime = (LocalDateTime) board.get("createdAt");
			List<Map<String, Object>> hits = boardMapper.getHits((long)board.get("boardCode"));
			board.put("hit", hits.size());
			String time = localdateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
			board.put("createdAt", time);
		}
		return boards;
	}
	
	public BoardDto getArticle(BoardDto boardDto) throws Exception{
		
		List<Map<String, Object>> boards = boardMapper.getHits(boardDto.getBoardCode());
		// hit한적이 없음
		if(boards.size() == 0) {
			boardMapper.updateHit(boardDto);
		}
		
		return boardMapper.getArticle(boardDto.getBoardCode());
	}
	
	public boolean writeArticle(BoardDto boardDto) throws Exception{
		return boardMapper.writeArticle(boardDto);
	}
	
	public boolean modifyArticle(BoardDto boardDto) throws Exception{
		return boardMapper.modifyArticle(boardDto);
	}
	
	public boolean deleteArticle(long boardCode) throws Exception{
		return boardMapper.deleteArticle(boardCode);
	}
	
	public long getCount() throws Exception{
		return boardMapper.getCount();
	}
	
	public List<BoardDto> recentFive() throws Exception{
		return boardMapper.recentFive();
	}
	
}

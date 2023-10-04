package com.liot.hob.controller;

import java.util.List;
import java.util.Map;

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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.liot.hob.model.BoardDto;
import com.liot.hob.model.NewsDto;
import com.liot.hob.model.ResponseCode;
import com.liot.hob.model.service.BoardService;
import com.liot.hob.model.service.NewsService;

@RestController
@CrossOrigin("*")
@RequestMapping("/board")
public class BoardController {
	
	public static final Logger logger = LoggerFactory.getLogger(MemberController.class);
	private final BoardService boardService;
	private final NewsService newsService;
	
	@Autowired
	public BoardController(BoardService boardService, NewsService newsService) {
		this.boardService = boardService;
		this.newsService = newsService;
	}
	
	/**
	 * 모든 글 목록을 불러온다.
	 * @return {@code ResponseEntity<ResponseCode>}
	 * @throws Exception
	 */
	@GetMapping
	public ResponseEntity<?> listArticle() throws Exception{
		ResponseCode responseCode;
		List<BoardDto> articles = boardService.listArticle();
		
		responseCode = new ResponseCode.builder<List<BoardDto>>().code("SUCCESS").items(articles).build();
		
		return new ResponseEntity<ResponseCode>(responseCode, HttpStatus.OK);
	}
	
	/**
	 * boardCode에 해당하는 게시글 반환
	 * @param boardCode
	 * @return {@code ResponseEntity<ResponseCode>}
	 * @throws Exception
	 */
	@PostMapping("/code")
	public ResponseEntity<?> getArticle(@RequestBody BoardDto boardDto) throws Exception{
		ResponseCode responseCode;
		BoardDto board = boardService.getArticle(boardDto);
//		boardService.updateHit(boardCode);
		
		responseCode = new ResponseCode.builder<BoardDto>().code("SUCCESS").items(board).build();
		return new ResponseEntity<ResponseCode>(responseCode, HttpStatus.OK);
	}
	
	@GetMapping("/page/{pageNo}")
	public ResponseEntity<?> getPageArticle(@PathVariable("pageNo") int pageNo) throws Exception{
		ResponseCode responseCode;
		
		List<Map<String, Object>> boards = boardService.getPageArticle(pageNo);

		responseCode = new ResponseCode.builder<List<Map<String, Object>>>().code("SUCCESS").items(boards).build();
		return new ResponseEntity<ResponseCode>(responseCode, HttpStatus.OK);
	}
	
	/**
	 * 글 작성한다.
	 * @param boardDto
	 * @return {@code ResponseEntity<ResponseCode>}
	 * @throws Exception
	 */
	@PostMapping
	public ResponseEntity<?> writeArticle(@RequestBody BoardDto boardDto) throws Exception{
		ResponseCode responseCode;
		System.out.println(boardDto);
		boolean result = boardService.writeArticle(boardDto);
		// 글쓰기에 성공했으면
		if(result) {
			responseCode = new ResponseCode.builder().code("SUCCESS").build();
		}
		// 글쓰기에 실패했으면
		else {
			responseCode = new ResponseCode.builder().code("FAIL").build();
		}
		return new ResponseEntity<ResponseCode>(responseCode, HttpStatus.OK);
	}
		
	/**
	 * 글 수정한다.
	 * @param boardDto
	 * @return {@code ResponseEntity<ResponseCode>}
	 * @throws Exception
	 */
	@PutMapping
	public ResponseEntity<?> modifyArticle(@RequestBody BoardDto boardDto) throws Exception{
		ResponseCode responseCode;
		boolean result = boardService.modifyArticle(boardDto);
		// 글수정에 성공했으면
		if(result) {
			responseCode = new ResponseCode.builder().code("SUCCESS").build();
		}
		// 글수정에 실패했으면
		else {
			responseCode = new ResponseCode.builder().code("FAIL").build();
		}
		return new ResponseEntity<ResponseCode>(responseCode, HttpStatus.OK);
		
	}
	
	/**
	 * 글 삭제한다.
	 * @param boardDto
	 * @return {@code ResponseEntity<ResponseCode>}
	 * @throws Exception
	 */
	@DeleteMapping("/{boardCode}")
	public ResponseEntity<?> deleteArticle(@PathVariable("boardCode") long boardCode) throws Exception{
		ResponseCode responseCode;
		boolean result = boardService.deleteArticle(boardCode);
		// 글삭제에 성공했으면
		if(result) {
			responseCode = new ResponseCode.builder().code("SUCCESS").build();
		}
		// 글삭제에 실패했으면
		else {
			responseCode = new ResponseCode.builder().code("FAIL").build();
		}
		return new ResponseEntity<ResponseCode>(responseCode, HttpStatus.OK);
	}
	
	@GetMapping("/count")
	public ResponseEntity<?> getCount() throws Exception{
		ResponseCode responseCode;
		long count = boardService.getCount();

		responseCode = new ResponseCode.builder<Long>().code("SUCCESS").items(count).build();

		return new ResponseEntity<ResponseCode>(responseCode, HttpStatus.OK);
	}
	
	@GetMapping("/recentfive")
	public ResponseEntity<?> boardRecentFive() throws Exception{
		ResponseCode responseCode;
		List<BoardDto> news = boardService.recentFive();

		responseCode = new ResponseCode.builder<List<BoardDto>>().code("SUCCESS").items(news).build();

		return new ResponseEntity<ResponseCode>(responseCode, HttpStatus.OK);
	}
	
	@GetMapping("/news/recentfive")
	public ResponseEntity<?> newsRecentFive() throws Exception{
		ResponseCode responseCode;
		List<NewsDto> news = newsService.recentFive();

		responseCode = new ResponseCode.builder<List<NewsDto>>().code("SUCCESS").items(news).build();

		return new ResponseEntity<ResponseCode>(responseCode, HttpStatus.OK);
	}
	
	
	@GetMapping("/news/getPageNews/{pageNo}")
	public ResponseEntity<?> getPageNews(@PathVariable("pageNo") int pageNo) throws Exception{
		ResponseCode responseCode;
		List<NewsDto> news = newsService.getPageNews(pageNo);

		responseCode = new ResponseCode.builder<List<NewsDto>>().code("SUCCESS").items(news).build();

		return new ResponseEntity<ResponseCode>(responseCode, HttpStatus.OK);
	}
	
	@GetMapping("/news/getCount")
	public ResponseEntity<?> getNewsCount() throws Exception{
		ResponseCode responseCode;
		long count = newsService.getCount();

		responseCode = new ResponseCode.builder<Long>().code("SUCCESS").items(count).build();

		return new ResponseEntity<ResponseCode>(responseCode, HttpStatus.OK);
	}
	
}

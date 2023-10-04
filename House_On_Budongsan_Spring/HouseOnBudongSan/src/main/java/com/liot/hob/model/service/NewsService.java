package com.liot.hob.model.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.liot.hob.model.NewsDto;
import com.liot.hob.model.mapper.NewsMapper;

@Service
public class NewsService {
	
	@Value("${naver.search.id}")
	private String naverId;
	
	@Value("${naver.search.secret}")
	private String naverSecret;
	
	private final String SEARCH_URL = "https://openapi.naver.com/v1/search/news.json";
	
	private NewsMapper mapper;
	
	@Autowired
	public NewsService(NewsMapper mapper) {
		this.mapper = mapper;
	}
	
	//최신 뉴스로 5개 가져오기
	public List<NewsDto> recentFive() throws Exception{
		return mapper.recentFive();
	}
	
	//뉴스 정보들 가져오기
	public List<NewsDto> newsList() throws Exception{
		return mapper.newsList();
	}
	
	//페이지별 뉴스정보 가져오기
	public List<NewsDto> getPageNews(int pageNo) throws Exception{
		int newPageNo = (pageNo-1) * 10;
		return mapper.getPageNews(newPageNo);
	}
	
	//모든 뉴스수를 가져온다
	public long getCount() throws Exception{
		return mapper.getCount();
	}
	
	//실제로 크롤링 한다
	@Scheduled(fixedDelay = 1000 * 60 * 5)	//5분마다 이 동작을 수행한다
	public void crawl() throws Exception {
		String url = SEARCH_URL + 
				"?query=" + URLEncoder.encode("부동산") + //항상 '부동산'이라는 키워드로 검색
				"&start=" + 1 + 						//항상 제일 처음거
				"&display=" + 100 + 					//고정 된 갯수만큼 가져오자
				"&sort=date";							//시간순으로 최신으로
		String jsonString = request(url);				//요청으로 부터 JSON String을 받아오고
		JSONObject json = new JSONObject(jsonString);	//json객체 생성
		
		int searchCount = json.getInt("display");	//몇 개의 결과가 있는지
		if(searchCount > 0) {
			//검색 결과가 있으면
			
			NewsDto recent = mapper.recentOne();		//제일 최신 뉴스를 하나 가져와서
			List<NewsDto> newNews = new LinkedList<>();	//크롤링 한 데이터 저장용
			
			JSONArray items = json.getJSONArray("items");	//실제 기사들의 배열을 가져와서
			for(int i = 0, limit=items.length(); i < limit; i++) {
				JSONObject newsObject = items.getJSONObject(i);	//하나의 기사들에 대한 json객체를 가져오기
				
				//각각의 정보들 가져오기
		        String title = newsObject.getString("title");
		        String description = newsObject.getString("description");
		        String originalLink = newsObject.getString("originallink");
		        String link = newsObject.getString("link");
		        
		        //시간 형식 맞춰주기
		        SimpleDateFormat inputDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
		        SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		        
		        //어차피 오류 안 나니까 그냥 던지자
		        Date publishDate = inputDateFormat.parse(newsObject.getString("pubDate"));	
	            String publishDateString = outputDateFormat.format(publishDate);

	            if(recent != null) {
	            	//만약 이미 저장된 뉴스 기사가 있으면 비교해보자
	            	if(recent.getPublishDate().equals(publishDateString) &&
	            			recent.getOriginalLink().equals(originalLink)) {
	            		//만약 출판날짜와 링크가 같으면
	            		// -> 동일한 기사일것이다
	            		break;
	            	}
	            }
	            
		        //새로운 객체를 만들어서, 값들 할당해주기
	            NewsDto news = new NewsDto();	
	            news.setTitle(title);
	            news.setDescription(description);
	            news.setOriginalLink(originalLink);
	            news.setLink(link);
	            news.setPublishDate(publishDateString);
	            
	            newNews.add(news);	//데이터를 리스트에 넣어서 추후 DB에 넣을 수 있도록 한다
			}
			
			if(newNews.size() != 0) {
				int result = mapper.insertMultiNews(newNews);	//실제로 넣어주자
				System.out.printf("Succes News Insert : %d\n", result);
			}
		}
	}
	
	//
	/**
	 * 실제로 request를 수행
	 * @param url 검색어 및 query를 포함한 String
	 * @return json문자열
	 */
	private String request(String url) {
		try {
			//URL객체 생성
			URL requestURL = new URL(url);
			//URL에 연결
			HttpURLConnection conn = (HttpURLConnection) requestURL.openConnection();
			
			//method및 header 설정
			conn.setRequestMethod("GET");
			conn.setRequestProperty("X-Naver-Client-Id", naverId);
			conn.setRequestProperty("X-Naver-Client-Secret", naverSecret);
			
			int responseCode = conn.getResponseCode();
			if(responseCode == HttpURLConnection.HTTP_OK) {
				//성공적으로 request가 완료되었으면
				BufferedReader in = new BufferedReader(new InputStreamReader(
                        conn.getInputStream()));
				
				String line = null;
                StringBuffer response = new StringBuffer();
				while( (line = in.readLine()) != null ) {
					//읽을 내용이 있는 동안
					//읽은 내용을 추가해주자
					response.append(line);
				}
				in.close();
				
				return response.toString();
			}
			else {
				//request에 실패한 경우
				return null;
			}
		}
		catch(Exception e) {
			//중간에 오류가 난 경우
			return null;
		}
	}
	
	
	
}

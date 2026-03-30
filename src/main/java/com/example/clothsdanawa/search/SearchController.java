package com.example.clothsdanawa.search;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.clothsdanawa.redis.RedisService;
import com.example.clothsdanawa.search.dto.SearchResponseDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/search")
public class SearchController {

	private final RedisService redisService;
	private final SearchService searchService;

	@GetMapping
	public ResponseEntity<List<SearchResponseDto>> keywordSearch(@RequestParam String keyword) {
		redisService.incrementCount(keyword);
		long start = System.currentTimeMillis();
		List<SearchResponseDto> searchResponseDtos = searchService.searchAll(keyword);
		long end = System.currentTimeMillis();
		System.out.println("search API 실행 시간(ms) = " + (end - start));

		return new ResponseEntity<>(searchResponseDtos, HttpStatus.OK);
	}

	@GetMapping("/top")
	public ResponseEntity<List<String>> getTop10Keywords() {
		List<String> top10Keywords = redisService.getTop10Keywords();
		return new ResponseEntity<>(top10Keywords, HttpStatus.OK);
	}
}

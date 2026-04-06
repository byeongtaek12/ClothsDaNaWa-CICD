package com.example.clothsdanawa.search;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
	public ResponseEntity<List<SearchResponseDto>> keywordSearch(
		@RequestParam String keyword,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size
	) {

		redisService.incrementCount(keyword);
		Pageable pageRequest = PageRequest.of(page, size);
		List<SearchResponseDto> searchResponseDtos = searchService.searchAll(keyword, pageRequest);
		return new ResponseEntity<>(searchResponseDtos, HttpStatus.OK);
	}

	@GetMapping("/top")
	public ResponseEntity<List<String>> getTop10Keywords() {
		List<String> top10Keywords = redisService.getTop10Keywords();
		return new ResponseEntity<>(top10Keywords, HttpStatus.OK);
	}
}

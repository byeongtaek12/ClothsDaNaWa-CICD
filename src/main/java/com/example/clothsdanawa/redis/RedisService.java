package com.example.clothsdanawa.redis;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RedisService {

	private final RedisTemplate<String, String> redisTemplate;

	public void incrementCount(String keyword) {
		//입력된 값이 있는지 검증
		if (keyword == null || keyword.isBlank())
			return;

		String todayHotKeywordsKey = todayHotKeywordsKey();
		//키워드 : score 값 저장
		//입력된 keyword 값 +1
		redisTemplate.opsForZSet().incrementScore(todayHotKeywordsKey, keyword, 1);

		Date expireAt = Date.from(
			LocalDate.now()
				.plusDays(1)
				.atStartOfDay(ZoneId.systemDefault())
				.toInstant()
		);

		redisTemplate.expireAt(todayHotKeywordsKey, expireAt);

	}

	//인기검색어 top10 가져오기
	public List<String> getTop10Keywords() {
		String todayHotKeywordsKey = todayHotKeywordsKey();

		Set<ZSetOperations.TypedTuple<String>> topKeywords = redisTemplate.opsForZSet()
			.reverseRangeWithScores(todayHotKeywordsKey, 0, 9);
		if (topKeywords == null)
			return List.of();

		return topKeywords.stream()
			.map(rank -> rank.getValue() + " : " + rank.getScore() + "회").toList();

	}

	private String todayHotKeywordsKey() {
		return "hot_keywords:" + LocalDate.now();
	}
}

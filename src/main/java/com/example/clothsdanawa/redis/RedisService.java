package com.example.clothsdanawa.redis;

import java.time.Duration;
import java.time.LocalDate;
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

	private static final String TODAY_HOT_KEYWORDS = "hot_keywords:" + LocalDate.now();

	public void incrementCount(String keyword) {
		//입력된 값이 있는지 검증
		if (keyword == null || keyword.isBlank())
			return;
		//키워드 : score 값 저장
		//입력된 keyword 값 +1
		redisTemplate.opsForZSet().incrementScore(TODAY_HOT_KEYWORDS, keyword, 1);

		//해당 키가 없을 경우에만 만료시간
		// if (!redisTemplate.hasKey(TODAY_HOT_KEYWORDS)) {
		// 	redisTemplate.expire(TODAY_HOT_KEYWORDS, Duration.ofDays(1));
		// }

		// Double score = redisTemplate.opsForZSet().score(TODAY_HOT_KEYWORDS, keyword);
		//
		// System.out.println(keyword + "의 검색 횟수 :" + score);

	}

	//인기검색어 top10 가져오기
	public List<String> getTop10Keywords() {
		Set<ZSetOperations.TypedTuple<String>> topKeywords = redisTemplate.opsForZSet()
			.reverseRangeWithScores(TODAY_HOT_KEYWORDS, 0, 9);
		if (topKeywords == null)
			return List.of();

		return topKeywords.stream()
			.map(rank -> rank.getValue() + " : " + rank.getScore() + "회").toList();

	}
}

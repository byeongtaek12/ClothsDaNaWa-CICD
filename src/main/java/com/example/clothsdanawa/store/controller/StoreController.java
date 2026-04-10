package com.example.clothsdanawa.store.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.clothsdanawa.common.security.CustomUserPrincipal;
import com.example.clothsdanawa.product.dto.response.ProductResponse;
import com.example.clothsdanawa.product.dto.response.ProductSliceResponse;
import com.example.clothsdanawa.redis.RedisService;
import com.example.clothsdanawa.store.dto.request.StoreCreateRequestDto;
import com.example.clothsdanawa.store.dto.request.StoreFilterRequestDto;
import com.example.clothsdanawa.store.dto.request.StoreUpdateRequestDto;
import com.example.clothsdanawa.store.dto.response.StoreResponseDto;
import com.example.clothsdanawa.store.dto.response.StoreSliceResponse;
import com.example.clothsdanawa.store.dto.response.VoidResponse;
import com.example.clothsdanawa.store.entity.Store;
import com.example.clothsdanawa.store.service.StoreService;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

@RequestMapping("/stores")
@RestController
@RequiredArgsConstructor
@Validated
public class StoreController {

	private final StoreService storeService;
	private final RedisService redisService;

	@PostMapping
	public ResponseEntity<VoidResponse> createStore(
		@AuthenticationPrincipal CustomUserPrincipal customUserPrincipal,
		@RequestBody StoreCreateRequestDto requestDto
	) {

		String email = customUserPrincipal.getUsername();

		storeService.createStore(requestDto, email);
		return new ResponseEntity<>(VoidResponse.from("쇼핑몰 등록을 요청하였습니다."), HttpStatus.OK);
	}

	@PutMapping("/{storeId}")
	public ResponseEntity<VoidResponse> updateStore(
		@AuthenticationPrincipal CustomUserPrincipal customUserPrincipal,
		@RequestBody StoreUpdateRequestDto storeUpdateRequestDto,
		@PathVariable Long storeId
	) {
		String email = customUserPrincipal.getUsername();

		storeService.updateStore(storeUpdateRequestDto, storeId, email);
		return new ResponseEntity<>(VoidResponse.from("쇼핑몰 정보를 수정하였습니다."),HttpStatus.OK);
	}

	@GetMapping
	public ResponseEntity<List<Store>> getStoreList(
		@RequestParam(defaultValue = "0") Long cursor,
		@RequestParam(required = false) String keyword
	) {
		StoreFilterRequestDto requestDto = new StoreFilterRequestDto(cursor - 1, keyword);
		List<Store> storeList = storeService.getStoreList(requestDto);
		return new ResponseEntity<>(storeList, HttpStatus.OK);
	}

	@GetMapping("/{storeId}")
	public ResponseEntity<StoreResponseDto> getStore(
		@PathVariable Long storeId
	) {
		StoreResponseDto responseDto = storeService.getStore(storeId);
		return new ResponseEntity<>(responseDto, HttpStatus.OK);
	}

	@PatchMapping("/{storeId}")
	public ResponseEntity<VoidResponse> closeStore(
		@AuthenticationPrincipal CustomUserPrincipal customUserPrincipal,
		@PathVariable Long storeId
	) {
		String email = customUserPrincipal.getUsername();

		storeService.closeStore(storeId, email);
		return new ResponseEntity<>(VoidResponse.from("쇼핑몰을 삭제하였습니다."),HttpStatus.OK);
	}

	@GetMapping("/search")
	public StoreSliceResponse<StoreResponseDto> getStoreByKeyword(
		@RequestParam @NotBlank String keyword,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size
	) {
		return storeService.getStoreByKeyword(keyword, page, size);
	}
}

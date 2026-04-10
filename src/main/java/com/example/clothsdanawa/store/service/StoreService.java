package com.example.clothsdanawa.store.service;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.clothsdanawa.common.exception.BaseException;
import com.example.clothsdanawa.common.exception.ErrorCode;
import com.example.clothsdanawa.store.dto.request.StoreCreateRequestDto;
import com.example.clothsdanawa.store.dto.request.StoreFilterRequestDto;
import com.example.clothsdanawa.store.dto.request.StoreUpdateRequestDto;
import com.example.clothsdanawa.store.dto.response.StoreResponseDto;
import com.example.clothsdanawa.store.dto.response.StoreSliceResponse;
import com.example.clothsdanawa.store.entity.Store;
import com.example.clothsdanawa.store.entity.StoreStatus;
import com.example.clothsdanawa.store.repository.StoreRepository;
import com.example.clothsdanawa.store.repository.StoreRepositoryQuery;
import com.example.clothsdanawa.user.entity.User;
import com.example.clothsdanawa.user.repository.UserRepository;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreService {
	private final StoreRepository storeRepository;
	private final StoreRepositoryQuery storeRepositoryQuery;
	private final UserRepository userRepository;

	@Transactional
	public void createStore(StoreCreateRequestDto requestDto, String email) {
		User user = userRepository.findByEmailAndDeletedAtIsNullOrElseThrow(email);
		Store store = Store.of(requestDto);
		store.setUser(user);
		storeRepository.save(store);
	}

	@Transactional
	public void approveStore(Long storeId) {
		Store pendingStore = storeRepository.findPendingStore(storeId);
		pendingStore.approveStore();
	}

	public List<Store> getPendingStore() {
		return storeRepository.findAllByStoreStatusOrderByCreatedAtAsc(StoreStatus.PENDING);
	}

	public List<Store> getStoreList(StoreFilterRequestDto requestDto) {
		return storeRepositoryQuery.findStoresByKeywordWithCursorOrderByUpdatedAt(requestDto);
	}

	public StoreResponseDto getStore(Long storeId) {
		Store store = storeRepository.findByStoreIdOrElseThrow(storeId);
		return StoreResponseDto.from(store);
	}

	@Transactional
	public void closeStore(Long storeId, String email) {
		User ownerUser = userRepository.findByEmailAndDeletedAtIsNullOrElseThrow(email);
		Store store = storeRepository.findByStoreIdOrElseThrow(storeId);
		if (store.getUser() != ownerUser) {
			throw new BaseException(ErrorCode.STORE_FORBIDDEN);
		}
		store.closeStore();
	}

	@Transactional
	public void updateStore(StoreUpdateRequestDto storeUpdateRequestDto, Long storeId, String email) {
		User ownerUser = userRepository.findByEmailAndDeletedAtIsNullOrElseThrow(email);
		Store store = storeRepository.findByStoreIdOrElseThrow(storeId);
		if (store.getUser() != ownerUser) {
			throw new BaseException(ErrorCode.STORE_FORBIDDEN);
		}
		store.setStore(storeUpdateRequestDto);
	}

	public StoreSliceResponse<StoreResponseDto> getStoreByKeyword(String keyword, int page, int size) {

		Pageable pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "storeId"));

		StoreStatus storeStatus = StoreStatus.OPEN;

		Slice<Store> storeSlice = storeRepository.findByCompanyContainingAndStoreStatus(keyword, pageRequest, storeStatus);

		List<StoreResponseDto> responseDtoList = storeSlice.getContent().stream()
			.map(StoreResponseDto::from)
			.toList();

		return new StoreSliceResponse<>(responseDtoList, storeSlice.getNumber(), storeSlice.getSize(),
			storeSlice.hasNext());
	}
}

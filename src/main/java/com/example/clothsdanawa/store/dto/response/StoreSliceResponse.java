package com.example.clothsdanawa.store.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StoreSliceResponse<T> {
	private List<T> content;
	private int page;
	private int size;
	private boolean hasNext;
}

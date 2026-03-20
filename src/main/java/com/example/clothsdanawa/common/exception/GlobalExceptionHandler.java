package com.example.clothsdanawa.common.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.persistence.EntityNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(EntityNotFoundException.class)
	public ResponseEntity<String> handleEntityNotFoundException(EntityNotFoundException e) {
		return ResponseEntity.status(404).body(e.getMessage());
	}

	@ExceptionHandler(BaseException.class)
	public ResponseEntity<ErrorResponse> handleBaseException(BaseException e) {
		return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(ErrorResponse.from(e.getErrorCode()));
	}

	/**
	 * 시스템 내부 예외 처리 (같이 쓰셔도 됩니다)
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<Map<String, Object>> handleException(Exception ex) {
		Map<String, Object> body = new HashMap<>();
		body.put("timestamp", LocalDateTime.now());
		body.put("code", ErrorCode.INTERNAL_SERVER_ERROR.getErrorCode());
		body.put("message", ErrorCode.INTERNAL_SERVER_ERROR.getMessage());

		return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getAllErrors().forEach(error -> {
			String fieldName = ((FieldError)error).getField();
			String errorMessage = error.getDefaultMessage();
			errors.put(fieldName, errorMessage);
		});

		ErrorResponse response = ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE, errors);
		return ResponseEntity.status(400).body(response);
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException e) {

		ConstraintViolationException cve = findHibernateConstraintViolation(e);
		if (cve != null) {
			String constraintName = cve.getConstraintName();
			if (constraintName != null) {
				if (constraintName.contains("uk_users_email")) {
					return conflict(ErrorCode.CONFLICT_EMAIL);
				}
			}
		}

		return conflict(ErrorCode.CONFLICT_USER_DATA);
	}

	private ResponseEntity<ErrorResponse> conflict(ErrorCode errorCode) {
		return ResponseEntity.status(HttpStatus.CONFLICT).body(ErrorResponse.from(errorCode));
	}

	private ConstraintViolationException findHibernateConstraintViolation(Throwable t) {
		while (t != null) {
			if (t instanceof ConstraintViolationException cve) return cve;
			t = t.getCause();
		}
		return null;
	}
}

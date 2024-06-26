package com.peoplehere.api.common.config;

import static org.springframework.http.HttpStatus.*;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.peoplehere.api.common.exception.AbusingException;
import com.peoplehere.api.common.exception.AccountIdNotFoundException;
import com.peoplehere.api.common.exception.ClientBindException;
import com.peoplehere.api.common.exception.DuplicateException;
import com.peoplehere.api.common.exception.ForbiddenException;
import com.peoplehere.api.common.exception.RequestLimitException;
import com.peoplehere.shared.common.data.response.ErrorResponseDto;
import com.peoplehere.shared.common.webhook.AlertWebhook;

import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class ErrorControllerAdvice {
	private final AlertWebhook alertWebhook;

	private static final String BAD_REQUEST_BODY = "잘못된 요청입니다!";
	private static final String FORBIDDEN_403_BODY = "접근할 수 없어요!";
	private static final String ERROR_500_BODY = "잠시 후 다시 확인해주세요!";

	@ExceptionHandler(value = {ForbiddenException.class, AccessDeniedException.class})
	public ResponseEntity<Object> redirect403(Exception exception) {
		log.debug("403 분석용", exception);
		return ResponseEntity.status(FORBIDDEN).body(FORBIDDEN_403_BODY);
	}

	@ExceptionHandler(value = DataIntegrityViolationException.class)
	public ResponseEntity<Object> handle422(Exception exception) {
		log.debug("422 에러분석용", exception);
		return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(ERROR_500_BODY);
	}

	/**
	 * 인증 예외의 핸들링
	 * @param exception
	 * @return
	 */
	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<ErrorResponseDto> handleAuthenticationException(BadCredentialsException exception) {
		log.debug("유효하지 않은 계정정보입니다.", exception);
		return ResponseEntity.badRequest().body(new ErrorResponseDto("유효하지않은 계정정보입니다"));
	}

	/**
	 * 클라이언트로부터의 요청 데이터 처리 중 발생한 바인딩 예외의 핸들링
	 * 예외 세부 정보를 응답으로 전달하지 않음
	 *
	 * @param exception
	 * @return
	 */
	@ExceptionHandler(value = ClientBindException.class)
	public ResponseEntity<ErrorResponseDto> handleClientBindException(ClientBindException exception) {
		log.error("바인딩 중 오류 발생", exception);
		return ResponseEntity.badRequest().body(new ErrorResponseDto("바인딩 중 오류 발생"));
	}

	/**
	 * 쿼리 스트링 요청 데이터 처리 중 발생한 예외의 핸들링
	 * 예외 세부 정보를 응답으로 전달함
	 *
	 * @param exception
	 * @return
	 */
	@ExceptionHandler(value = ConstraintViolationException.class)
	public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException exception) {
		log.error("쿼리스트링 예외 핸들링", exception);
		return ResponseEntity.badRequest().body(BAD_REQUEST_BODY);
	}

	/**
	 * 관리자의(백오피스) 내부에서 요청 데이터 처리 중 발생한 바인딩 예외의 핸들링
	 * 예외 세부 정보를 응답으로 전달함
	 *
	 * @param exception
	 * @return
	 */
	@ExceptionHandler(value = BindException.class)
	public ResponseEntity<ErrorResponseDto> handleBindException(BindException exception) {
		log.error("바인딩 중 오류 발생", exception);
		alertWebhook.alertError("바인딩 중 오류 발생", exception.getMessage());
		ErrorResponseDto response = new ErrorResponseDto("바인딩 중 오류 발생", exception);
		return ResponseEntity.badRequest().body(response);
	}

	/**
	 * 중복된 값이 발생했을 때의 핸들링
	 * @param exception
	 * @return
	 */
	@ExceptionHandler(value = DuplicateException.class)
	public ResponseEntity<ErrorResponseDto> handleDuplicateException(DuplicateException exception) {
		log.debug("중복된 값 발생", exception);
		ErrorResponseDto response = new ErrorResponseDto(exception);
		return ResponseEntity.status(CONFLICT).body(response);
	}

	/**
	 * 계정 아이디를 찾을 수 없을 때의 핸들링(로그인, 이메일 체크 용)
	 * @param exception
	 * @return
	 */
	@ExceptionHandler(value = AccountIdNotFoundException.class)
	public ResponseEntity<ErrorResponseDto> handleAccountIdNotFoundException(AccountIdNotFoundException exception) {
		log.debug("계정 아이디를 찾을 수 없음", exception);
		ErrorResponseDto response = new ErrorResponseDto(exception);
		return ResponseEntity.status(NOT_FOUND).body(response);
	}

	/**
	 * 동일 사용자의 과다 요청이 발생하면 429 응답을 보냄
	 */
	@ExceptionHandler(value = AbusingException.class)
	public ResponseEntity<Object> handleAbusing(AbusingException exception) {
		log.warn(exception.getMessage());
		return ResponseEntity.status(TOO_MANY_REQUESTS).body(ERROR_500_BODY);
	}

	/**
	 * 요청 제한 api에 대한 동일 사용자의 초과 요청 발생하면 403 응답을 보냄
	 */
	@ExceptionHandler(value = RequestLimitException.class)
	public ResponseEntity<Object> handleRequestLimit(RequestLimitException exception) {
		log.warn(exception.getMessage());
		return ResponseEntity.status(FORBIDDEN).body(FORBIDDEN_403_BODY);
	}

	/**
	 * 그 외의 예외의 핸들링
	 * @param exception
	 * @return
	 */
	@ExceptionHandler(value = Exception.class)
	public ResponseEntity<Object> handle(Exception exception) {
		log.debug("500 에러분석용", exception);
		alertWebhook.alertError("500 에러 발생 분석을 하라", exception.getMessage());
		return ResponseEntity.internalServerError().body(ERROR_500_BODY);
	}
}

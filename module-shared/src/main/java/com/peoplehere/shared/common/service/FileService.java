package com.peoplehere.shared.common.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {

	/**
	 * 파일을 저장
	 */
	String uploadFileAndGetFileInfo(long accountId, MultipartFile file);

	/**
	 * 파일 삭제
	 */
	void deleteFile();

}

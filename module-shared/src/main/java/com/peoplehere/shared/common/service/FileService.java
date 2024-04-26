package com.peoplehere.shared.common.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {

	/**
	 * 파일을 저장
	 */
	String uploadFileAndGetFileInfo(long id, MultipartFile file);

	List<String> uploadFileListAndGetFileInfoList(long id, List<MultipartFile> fileList);

	/**
	 * 파일 삭제
	 */
	void deleteFile();

}

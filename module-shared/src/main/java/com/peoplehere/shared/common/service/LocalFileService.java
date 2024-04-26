package com.peoplehere.shared.common.service;

import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Profile("local")
@RequiredArgsConstructor
public class LocalFileService implements FileService {

	@Override
	public String uploadFileAndGetFileInfo(long accountId, MultipartFile file) {
		return null;
	}

	@Override
	public List<String> uploadFileListAndGetFileInfoList(long id, List<MultipartFile> fileList) {
		return null;
	}

	@Override
	public void deleteFile() {

	}
}

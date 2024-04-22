package com.peoplehere.shared.common.service;

import static com.peoplehere.shared.common.config.file.FileKeyProperties.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import org.apache.commons.io.FilenameUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.peoplehere.shared.common.config.file.S3FileProperties;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@Slf4j
@Profile("!local & !test")
@RequiredArgsConstructor
public class S3FileService implements FileService {

	private final S3Client s3Client;
	private final S3FileProperties s3FileProperties;

	@Override
	public String uploadFileAndGetFileInfo(long accountId, MultipartFile file) {
		String fileName = file.getOriginalFilename();
		String key = generateProfileImageKey(s3FileProperties.getPrefix(), accountId,
			FilenameUtils.getExtension(fileName));

		PutObjectRequest putObjectRequest = PutObjectRequest.builder()
			.bucket(s3FileProperties.getBucketName())
			.cacheControl("public, max-age=2592000") // 60 * 60 * 24 * 30
			.contentDisposition("inline; filename=\""
				+ URLEncoder.encode(Objects.requireNonNull(fileName), StandardCharsets.UTF_8).replace("+", "%20")
				+ "\"")
			.contentType(file.getContentType())
			.key(key)
			.build();

		try {
			s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));
		} catch (IOException e) {
			log.info("파일 업로드 중 에러 발생: {}", file.getOriginalFilename());
			throw new RuntimeException(e);
		}
		return toCloudFrontUrl(getFileUrl(s3FileProperties.getBucketName(), key));

	}

	@Override
	public void deleteFile() {

	}

	private String getFileUrl(String bucketName, String key) {
		return s3Client.utilities().getUrl(
			GetUrlRequest.builder()
				.bucket(bucketName)
				.key(key)
				.build()
		).toString();
	}

	private String toCloudFrontUrl(String url) {
		if (url == null) {
			return null;
		}
		if (s3FileProperties.getReplaceSrc() == null || s3FileProperties.getReplaceDest() == null) {
			return url;
		}
		return url.replace(s3FileProperties.getReplaceSrc(), s3FileProperties.getReplaceDest());
	}
}

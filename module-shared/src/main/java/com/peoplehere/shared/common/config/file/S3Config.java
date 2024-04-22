package com.peoplehere.shared.common.config.file;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.util.StringUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProviderChain;
import software.amazon.awssdk.auth.credentials.InstanceProfileCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class S3Config {

	private final S3FileProperties s3FileProperties;

	@Profile("!test")
	@Bean
	S3Client s3Client() {
		var providerChainBuilder = AwsCredentialsProviderChain.builder();

		// 1순위: 프로퍼티 값
		if (StringUtils.hasText(s3FileProperties.accessKey) && StringUtils.hasText(s3FileProperties.secretKey)) {
			var credentials = AwsBasicCredentials.create(s3FileProperties.accessKey, s3FileProperties.secretKey);
			var staticProvider = StaticCredentialsProvider.create(credentials);
			providerChainBuilder.addCredentialsProvider(staticProvider);
		}

		// 2순위: 특정 profile name의 aws credential 인증 정보 있는 경우
		if (StringUtils.hasText(s3FileProperties.profileName)) {
			var profileProvider = ProfileCredentialsProvider.create(s3FileProperties.profileName);
			providerChainBuilder.addCredentialsProvider(profileProvider);
		}

		// 3순위: EC2에 인증 정보 있는 경우
		var instanceProvider = InstanceProfileCredentialsProvider.create();
		providerChainBuilder.addCredentialsProvider(instanceProvider);

		var providerChain = providerChainBuilder.build();

		log.info("accessKey: {} | profileName: {}", s3FileProperties.accessKey, s3FileProperties.profileName);

		return S3Client.builder().region(Region.of(s3FileProperties.region))
			.credentialsProvider(providerChain).build();
	}

}

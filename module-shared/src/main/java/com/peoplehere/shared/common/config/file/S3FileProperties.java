package com.peoplehere.shared.common.config.file;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "service.s3.file")
public class S3FileProperties {

	protected String type;
	protected String prefix;
	protected String bucketName;
	protected String region;
	protected String accessKey;
	protected String secretKey;
	protected String profileName;
	protected String replaceSrc;
	protected String replaceDest;
}

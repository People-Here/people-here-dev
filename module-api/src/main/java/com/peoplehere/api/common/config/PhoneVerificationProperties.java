package com.peoplehere.api.common.config;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@Component
@ConfigurationProperties(prefix = "verify.twilio")
public class PhoneVerificationProperties {
	protected String accountSid;

	protected String authToken;

	protected String fromNumber;

	protected String baseUrl;

	protected String smsUri;

	public String getSmsUrl() {
		return this.baseUrl + this.accountSid + this.smsUri;
	}

	public String getEncodedAuthStr() {
		return "Basic " + Base64.getEncoder()
			.encodeToString((this.accountSid + ":" + this.authToken).getBytes(StandardCharsets.UTF_8));
	}
}

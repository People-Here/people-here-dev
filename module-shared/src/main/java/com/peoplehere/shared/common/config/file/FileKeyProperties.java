package com.peoplehere.shared.common.config.file;

import static com.peoplehere.shared.common.enums.ImageType.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import lombok.experimental.UtilityClass;

@UtilityClass
public class FileKeyProperties {

	public static String generateProfileImageKey(String serverPrefix, long id, String extension) {
		return "%s/%s/%s/%s-%s.%s".formatted(
			serverPrefix,
			id,
			PROFILE_IMAGE.getFilePathPrefix(),
			LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd/HH")),
			UUID.randomUUID(),
			extension
		);
	}

	public static String generateTourImageKey(String serverPrefix, long id, String extension) {
		return "%s/%s/%s/%s-%s.%s".formatted(
			serverPrefix,
			id,
			TOUR_IMAGE.getFilePathPrefix(),
			LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd/HH")),
			UUID.randomUUID(),
			extension
		);
	}
}

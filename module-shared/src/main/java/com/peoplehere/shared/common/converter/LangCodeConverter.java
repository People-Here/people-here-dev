package com.peoplehere.shared.common.converter;

import static java.util.stream.Collectors.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.peoplehere.shared.common.enums.LangCode;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class LangCodeConverter implements AttributeConverter<List<LangCode>, String> {

	private static final String SPLIT_CHAR = ",";

	@Override
	public String convertToDatabaseColumn(List<LangCode> attribute) {
		if (attribute == null || attribute.isEmpty()) {
			return "";
		}
		return attribute.stream()
			.map(LangCode::getKoreanName)
			.collect(joining(SPLIT_CHAR));
	}

	@Override
	public List<LangCode> convertToEntityAttribute(String dbData) {
		if (dbData == null || dbData.trim().isEmpty()) {
			return Collections.emptyList();
		}
		return Arrays.stream(dbData.split(SPLIT_CHAR))
			.map(LangCode::fromKoreanName)
			.collect(toList());
	}
}

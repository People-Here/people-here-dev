package com.peoplehere.shared.common.service;

import java.util.List;

import com.peoplehere.shared.common.data.response.TranslationUsageResponseDto;
import com.peoplehere.shared.common.enums.LangCode;

public interface TranslateService {

	String translate(List<String> srcList, LangCode langCode);

	TranslationUsageResponseDto getUsage();
}

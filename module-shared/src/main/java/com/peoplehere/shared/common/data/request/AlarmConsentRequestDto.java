package com.peoplehere.shared.common.data.request;

import com.peoplehere.shared.common.enums.Alarm;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AlarmConsentRequestDto {

	@NotNull
	private Alarm alarm;

	@NotNull
	private boolean consent;
}

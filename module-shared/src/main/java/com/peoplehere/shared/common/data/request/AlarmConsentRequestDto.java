package com.peoplehere.shared.common.data.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AlarmConsentRequestDto {

	@NotNull
	private boolean consent;
}

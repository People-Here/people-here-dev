package com.peoplehere.api.common.controller;

import static com.peoplehere.shared.common.enums.Region.*;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.peoplehere.shared.common.data.response.RegionResponseDto;
import com.peoplehere.shared.common.enums.Gender;

@RestController
@RequestMapping("/api/constants")
public class ConstantController {

	@GetMapping("/genders")
	public ResponseEntity<Gender[]> getGenderType() {
		return ResponseEntity.ok(Gender.VALUES);
	}

	/**
	 * 국가 코드, 영문 이름, 한글 이름, 국제 전화 코드를 반환
	 * @return
	 */
	@GetMapping("/regions")
	public ResponseEntity<List<RegionResponseDto>> getRegions() {
		return ResponseEntity.ok(REGION_INFO_LIST);
	}
}


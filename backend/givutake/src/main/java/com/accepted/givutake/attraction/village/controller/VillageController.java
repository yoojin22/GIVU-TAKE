package com.accepted.givutake.attraction.village.controller;

import com.accepted.givutake.attraction.village.entity.ExperienceVillage;
import com.accepted.givutake.attraction.village.model.ExperienceVillageViewDto;
import com.accepted.givutake.attraction.village.service.ExperienceVillageService;
import com.accepted.givutake.global.model.ResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/experience-village")
public class VillageController {

    private final ExperienceVillageService experienceVillageService;

    // 조건에 맞는 모든 체험마을 정보 조회
    @GetMapping
    public ResponseEntity<ResponseDto> getExperienceVillage(@RequestParam String sido,
                                                            @RequestParam String sigungu,
                                                            @RequestParam(required = false) String division,
                                                            @RequestParam(defaultValue = "0") int pageNo,
                                                            @RequestParam(defaultValue = "10") int pageSize) {

        Page<ExperienceVillage> savedExperienceVillages =experienceVillageService.getExperienceVillage(sido, sigungu, division, pageNo, pageSize);
        List<ExperienceVillageViewDto> experienceVillageViewDtoList = savedExperienceVillages.getContent()
                .stream()
                .map(ExperienceVillageViewDto::toDto)
                .collect(Collectors.toList());

        ResponseDto responseDto = ResponseDto.builder()
                .data(experienceVillageViewDtoList)
                .build();

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
}

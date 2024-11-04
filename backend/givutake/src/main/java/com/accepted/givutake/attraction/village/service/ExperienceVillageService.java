package com.accepted.givutake.attraction.village.service;

import com.accepted.givutake.attraction.village.entity.ExperienceVillage;
import com.accepted.givutake.attraction.village.repository.ExperienceVillageRepository;
import com.accepted.givutake.region.entity.Region;
import com.accepted.givutake.region.service.RegionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ExperienceVillageService {

    private final ExperienceVillageRepository experienceVillageRepository;
    private final RegionService regionService;

    // 조건에 맞는 모든 체험마을 정보 조회
    public Page<ExperienceVillage> getExperienceVillage(String sido, String sigungu, String division, int pageNo, int pageSize) {
        // 1. 지역 정보 조회
        Region savedRegion = regionService.findRegionBySidoAndSigungu(sido, sigungu);

        // 지역 정보가 없다면 빈 값 반환
        if (savedRegion == null) {
            return Page.empty();
        }

        Pageable pageable = PageRequest.of(pageNo, pageSize);

        // division 값이 없다면 Region 값에 해당하는 모든  체험 마을 정보 반환
        if (division == null) {
            return experienceVillageRepository.findByRegionIdx(savedRegion.getRegionIdx(), pageable);
        }

        return experienceVillageRepository.findByRegionIdxAndExperienceVillageDivisionLike(savedRegion.getRegionIdx(), division, pageable);
    }
}

package com.accepted.givutake.region.service;

import com.accepted.givutake.global.enumType.ExceptionEnum;
import com.accepted.givutake.global.exception.ApiException;
import com.accepted.givutake.region.entity.Region;
import com.accepted.givutake.region.repository.RegionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RegionService {

    private final RegionRepository regionRepository;

    // sido와 sigungu 필드에 해당하는 regionIdx 반환
    public int getRegionIdxBySidoAndSigungu(String sido, String sigungu) {
        Integer regionIdx = regionRepository.findRegionIdxBySidoAndSigungu(sido, sigungu);

        if (regionIdx == null) {
            throw new ApiException(ExceptionEnum.NOT_FOUND_REGION_EXCEPTION);
        }

        return regionIdx;
    }

    public Region findRegionBySidoAndSigungu(String sido, String sigungu) {
        Optional<Region> optionalRegion = regionRepository.findRegionBySidoAndSigungu(sido, sigungu);

        if (optionalRegion.isPresent()) {
            return optionalRegion.get();
        }

        throw new ApiException(ExceptionEnum.NOT_FOUND_REGION_EXCEPTION);
    }

    // regionIdx에 해당하는 데이터가 있는지 반환
    public boolean existsByRegionIdx(int regionIdx) {
        return regionRepository.existsByRegionIdx(regionIdx);
    }

    // 모든 시/도 명을 반환
    public List<String> getSidoList() {
        return regionRepository.findDistinctSido();
    }

    // 시/도명에 해당하는 모든 시군구 리스트 반환
    public List<String> getSigunguListBySido(String sido) {
        return regionRepository.findSigunguBySido(sido);
    }
}

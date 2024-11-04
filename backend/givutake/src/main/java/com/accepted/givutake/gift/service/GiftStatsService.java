package com.accepted.givutake.gift.service;

import com.accepted.givutake.gift.model.GiftPercentageDto;
import com.accepted.givutake.gift.repository.GiftPercentageRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class GiftStatsService {

    private final GiftPercentageRepository giftPercentageRepository;
    public GiftPercentageDto getGiftPercentage(Integer giftIdx, long userIdx) {
        List<Object[]> results;
        if (giftIdx == null) {
            results = giftPercentageRepository.getOverallGiftStatistics();
        } else {
            results = giftPercentageRepository.getGiftStatisticsByGiftId(giftIdx, userIdx);
        }

        GiftPercentageDto giftPercentageDto = new GiftPercentageDto();
        Map<String, Map<String, GiftPercentageDto.StatDto>> statistics = new HashMap<>();
        statistics.put("gender", new HashMap<>());
        statistics.put("age", new HashMap<>());

        statistics.get("gender").put("male", new GiftPercentageDto.StatDto(0L, 0.0));
        statistics.get("gender").put("female", new GiftPercentageDto.StatDto(0L, 0.0));

        List.of("20s", "30s", "40s", "50s", "60+").forEach( age -> statistics.get("age").put(age, new GiftPercentageDto.StatDto(0L, 0.0)));

        for (Object[] row : results) {
            String statType = (String) row[0];
            String name = (String) row[1];
            Long count = ((Number) row[2]).longValue();
            Double percentage = ((Number) row[3]).doubleValue();

            GiftPercentageDto.StatDto statDto = new GiftPercentageDto.StatDto(count, percentage);

            statistics.computeIfAbsent(statType, k -> new HashMap<>()).put(name, statDto);
        }

        giftPercentageDto.setStatistics(statistics);
        return giftPercentageDto;
    }

}

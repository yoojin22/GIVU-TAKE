package com.accepted.givutake.funding.service;

import com.accepted.givutake.funding.entity.Fundings;
import com.accepted.givutake.funding.model.FundingDayStatisticDto;
import com.accepted.givutake.funding.model.FundingParticipateDto;
import com.accepted.givutake.funding.model.FundingStatsByAgeAndGenderDto;
import com.accepted.givutake.funding.model.participant;
import com.accepted.givutake.funding.repository.FundingRepository;
import com.accepted.givutake.funding.repository.FundingStatisticsRepository;
import com.accepted.givutake.global.enumType.ExceptionEnum;
import com.accepted.givutake.global.exception.ApiException;
import com.accepted.givutake.payment.entity.FundingParticipants;
import com.accepted.givutake.payment.repository.FundingParticipantsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class FundingStatsService {

    private final FundingRepository fundingRepository;
    private final FundingParticipantsRepository fundingParticipantsRepository;
    private final FundingStatisticsRepository fundingStatisticsRepository;

    public FundingDayStatisticDto getFundingDayStatisticByFundingIdx(String email, int fundingIdx) {
        Fundings funding = fundingRepository.findByFundingIdx(fundingIdx).orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_FUNDING_WITH_IDX_EXCEPTION));
        if(!funding.getCorporation().getEmail().equals(email)) {
            throw new ApiException(ExceptionEnum.ACCESS_DENIED_EXCEPTION);
        }
        if(funding.getState() == 0){
            throw new ApiException(ExceptionEnum.NOT_ALLOWED_FUNDING_IN_BEFORE_STATISTICS_EXCEPTION);
        }
        if(funding.getState() == 1){
            throw new ApiException(ExceptionEnum.NOT_ALLOWED_FUNDING_IN_PROCESS_STATISTICS_EXCEPTION);
        }
        LocalDate startDate = funding.getStartDate();
        LocalDate endDate = funding.getEndDate();
        int days = (int) ChronoUnit.DAYS.between(startDate, endDate) + 1;
        int[] arr = new int[days];


        List<FundingParticipants> participants = funding.getFundingParticipantsList();


        for (FundingParticipants participant : participants) {
            LocalDate participationDate = participant.getCreatedDate().toLocalDate();
            if (!participationDate.isBefore(startDate) && !participationDate.isAfter(endDate)) {
                int dayIndex = (int) ChronoUnit.DAYS.between(startDate, participationDate);
                arr[dayIndex] += participant.getFundingFee();
            }
        }
        return new FundingDayStatisticDto(arr);
    }

    public FundingParticipateDto getFundingParticipateByFundingIdx(String email, int fundingIdx) {
        Fundings funding = fundingRepository.findByFundingIdx(fundingIdx).orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_FUNDING_WITH_IDX_EXCEPTION));
        if(!funding.getCorporation().getEmail().equals(email)) {
            throw new ApiException(ExceptionEnum.ACCESS_DENIED_EXCEPTION);
        }
        if(funding.getState() == 0){
            throw new ApiException(ExceptionEnum.NOT_ALLOWED_FUNDING_IN_BEFORE_STATISTICS_EXCEPTION);
        }
        if(funding.getState() == 1){
            throw new ApiException(ExceptionEnum.NOT_ALLOWED_FUNDING_IN_PROCESS_STATISTICS_EXCEPTION);
        }
        List<Object[]> participateData = fundingParticipantsRepository.findFundingParticipantsByFundingIdx(fundingIdx);

        List<participant> participants= participateData.stream()
                .map(data -> new participant(
                        (String) data[0],
                        ((Number) data[1]).intValue()
                ))
                .sorted(Comparator.comparingInt(participant::getPrice).reversed())
                .collect(Collectors.toList());

        return new FundingParticipateDto(participants);
    }

    public FundingStatsByAgeAndGenderDto getFundingCountByAgeAndGender(String email, int fundingIdx) {
        Fundings funding = fundingRepository.findByFundingIdx(fundingIdx).orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_FUNDING_WITH_IDX_EXCEPTION));
        if(!funding.getCorporation().getEmail().equals(email)) {
            throw new ApiException(ExceptionEnum.ACCESS_DENIED_EXCEPTION);
        }
        if(funding.getState() == 0){
            throw new ApiException(ExceptionEnum.NOT_ALLOWED_FUNDING_IN_BEFORE_STATISTICS_EXCEPTION);
        }
        if(funding.getState() == 1){
            throw new ApiException(ExceptionEnum.NOT_ALLOWED_FUNDING_IN_PROCESS_STATISTICS_EXCEPTION);
        }

        List<Object[]> results = fundingStatisticsRepository.getFundingStatsByAgeAndGender(fundingIdx);

        FundingStatsByAgeAndGenderDto result = new FundingStatsByAgeAndGenderDto();

        HashMap<String, Long> maleData = new HashMap<>();
        HashMap<String, Long> femaleData = new HashMap<>();

        String[] ageGroups = { "20s", "30s", "40s", "50s", "60+" };

        for (String ageGroup : ageGroups) {
            maleData.put(ageGroup, 0L);
            femaleData.put(ageGroup, 0L);
        }

        results.forEach(row -> {
                    String gender = (String) row[0];
                    String ageGroup = (String) row[1];
                    Long count = ((Number) row[2]).longValue();

                    if ("male".equals(gender)) {
                        maleData.put(ageGroup, count);
                    } else {
                        femaleData.put(ageGroup, count);
                    }
                }
        );

        result.setMaleData(maleData);
        result.setFemaleData(femaleData);

        return result;
    }
}

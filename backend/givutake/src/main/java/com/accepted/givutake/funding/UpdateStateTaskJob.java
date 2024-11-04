package com.accepted.givutake.funding;

import com.accepted.givutake.funding.entity.Fundings;
import com.accepted.givutake.funding.repository.FundingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class UpdateStateTaskJob implements Job {

    private final FundingRepository fundingRepository;

    @Override
    public void execute(JobExecutionContext context) {
        log.info("펀딩 state 변경 작업이 {}에 시작되었습니다.", LocalDateTime.now());
        // 모금 대기 중인 펀딩 리스트 조회
        List<Fundings> watingFundingList = fundingRepository.findByIsDeletedFalseAndState((byte) 0);

        // 현재 날짜가 모금 시작일이거나 이후일 때 모금 중 상태로 변경
        for (Fundings fundings : watingFundingList) {
            if (fundings.getStartDate().isBefore(LocalDate.now()) || fundings.getStartDate().equals(LocalDate.now())) {
                fundings.setState((byte) 1);
                fundingRepository.save(fundings);
            }
        }

        // 모금 중인 펀딩 리스트 조회
        List<Fundings> inProgressFundingList = fundingRepository.findByIsDeletedFalseAndState((byte) 1);

        // 현재 날짜가 모금 종료일 이후일 때 모금 종료 상태로 변경
        for (Fundings fundings : inProgressFundingList) {
            if (fundings.getEndDate().isBefore(LocalDate.now())) {
                fundings.setState((byte) 2);
                fundingRepository.save(fundings);
            }
        }

        log.info("펀딩 state 변경 작업이 {}에 완료되었습니다.", LocalDateTime.now());
    }
}
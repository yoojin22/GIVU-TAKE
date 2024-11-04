package com.accepted.givutake.funding.service;

import com.accepted.givutake.payment.entity.FundingParticipants;
import com.accepted.givutake.payment.repository.FundingParticipantsRepository;
import com.accepted.givutake.user.common.entity.Users;
import com.accepted.givutake.user.common.model.UserDto;
import com.accepted.givutake.user.common.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class FundingParticipantService {

    private final FundingParticipantsRepository fundingParticipantsRepository;
    private final UserService userService;

    // 일정 기간 동안의 자신의 펀딩 내역 조회
    public List<FundingParticipants> getFundingParticipantsListByEmail(String email, LocalDate startDate, LocalDate endDate) {
        // 1. user 조회
        UserDto savedUserDto = userService.getUserByEmail(email);
        Users savedUsers = savedUserDto.toEntity();

        // 2. startDate, endDate를 LocalDateTime으로 변경
        LocalDateTime startDateTime;
        LocalDateTime endDateTime;

        // 2. startDate와 endDate 값이 빈 값일 경우 전체조회
        if (endDate == null && startDate == null) {
            return fundingParticipantsRepository.findByUsers(savedUsers);
        }

        if (startDate == null) {
            endDateTime = endDate.atTime(23, 59, 59);  // 해당 날짜의 23:59:59
            return fundingParticipantsRepository.findByCreatedDateBefore(endDateTime);
        }

        if (endDate == null) {
            startDateTime = startDate.atStartOfDay();
            return fundingParticipantsRepository.findByCreatedDateAfter(startDateTime);
        }

        endDateTime = endDate.atTime(23, 59, 59);
        startDateTime = startDate.atStartOfDay();

        return fundingParticipantsRepository.findByUsersAndCreatedDateBetween(savedUsers, startDateTime, endDateTime);
    }

    // 자신이 참여한 펀딩 수 조회
    public long getCountByEmail(String email) {
        // 1. DB에서 user 가져오기
        UserDto savedUserDto = userService.getUserByEmail(email);
        Users savedUser = savedUserDto.toEntity();

        // 2. 펀딩 수 조회
        return fundingParticipantsRepository.countByUsers(savedUser);
    }

    // 자신이 참여한 모든 펀딩의 기부금 조회
    public long calculateTotalFundingFeeByEmail(String email) {
        // 1. DB에서 user 가져오기
        UserDto savedUserDto = userService.getUserByEmail(email);
        Users savedUser = savedUserDto.toEntity();

        // 2. 펀딩 기부금 조회
        Long sum = fundingParticipantsRepository.sumFundingFeeByUserIdx(savedUser.getUserIdx());

        if (sum == null) {
            return 0;
        }

        return sum;
    }

    // 모든 사용자가 참여한 모든 펀딩 기부금 조회
    public long calculateTotalFundingFee() {
        // 1. 펀딩 기부금 조회
        Long sum = fundingParticipantsRepository.sumFundingFee();

        if (sum == null) {
            return 0;
        }

        return sum;
    }
}

package com.accepted.givutake.funding.controller;

import com.accepted.givutake.funding.entity.CheerComments;
import com.accepted.givutake.funding.entity.Fundings;
import com.accepted.givutake.funding.model.*;
import com.accepted.givutake.funding.service.CheerCommentService;
import com.accepted.givutake.funding.service.FundingReviewService;
import com.accepted.givutake.funding.service.FundingService;
import com.accepted.givutake.funding.service.FundingStatsService;
import com.accepted.givutake.global.enumType.ExceptionEnum;
import com.accepted.givutake.global.exception.ApiException;
import com.accepted.givutake.global.model.ResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/government-fundings")
public class FundingController {

    private final CheerCommentService cheerCommentService;
    private final FundingService fundingService;
    private final FundingReviewService fundingReviewService;
    private final FundingStatsService fundingStatsService;

    // ========= 펀딩 관련 ===========
    // 자신이 작성한 모든 펀딩 조회
    @GetMapping("/my-fundings")
    public ResponseEntity<ResponseDto> getMyFundingList(@AuthenticationPrincipal UserDetails userDetails,
                                                        @RequestParam(required = false) Byte state,
                                                        @RequestParam(required = false) Character type,
                                                        @RequestParam(required = false, defaultValue = "0") int pageNo,
                                                        @RequestParam(required = false, defaultValue = "10") int pageSize) {

        String email = userDetails.getUsername();

        List<FundingViewDto> fundingViewDtoList =
                fundingService.getMyFundingList(email, state, type, pageNo, pageSize)
                        .stream()
                        .map(FundingViewDto::toDto)
                        .collect(Collectors.toList());

        ResponseDto responseDto = ResponseDto.builder()
                .data(fundingViewDtoList)
                .build();

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    // 조건에 해당하는 모든 펀딩 조회
    @GetMapping
    public ResponseEntity<ResponseDto> getFundingList(@RequestParam char type, @RequestParam byte state) {

        List<FundingViewDto> fundingViewDtoList =
                fundingService.getFundingByTypeAndState(type, state)
                        .stream()
                        .map(FundingViewDto::toDto)  // 각 CheerComments 객체를 CheerCommentViewDto로 변환
                        .collect(Collectors.toList());  // List로 변환

        ResponseDto responseDto = ResponseDto.builder()
                .data(fundingViewDtoList)
                .build();

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    // fundingIdx에 해당하는 펀딩 상세 조회
    @GetMapping("/{fundingIdx}")
    public ResponseEntity<ResponseDto> getFundingByFundingIdx(@PathVariable int fundingIdx) {

        Fundings savedFundings = fundingService.getFundingByFundingIdx(fundingIdx);
        FundingDetailViewDto fundingDetailViewDto = FundingDetailViewDto.toDto(savedFundings);

        ResponseDto responseDto = ResponseDto.builder()
                .data(fundingDetailViewDto)
                .build();

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    // jwt 토큰으로 펀딩 등록
    @PostMapping
    public ResponseEntity<ResponseDto> addFundingByJwt(@AuthenticationPrincipal UserDetails userDetails,
                                                       @RequestPart(value = "fundingAddDto") @Valid FundingAddDto fundingAddDto,
                                                       @RequestPart(value = "fundingThumbnail", required = false) MultipartFile fundingThumbnail,
                                                       @RequestPart(value = "contentImage", required = false) MultipartFile contentImage) {
        String email = userDetails.getUsername();

        if (!(fundingAddDto.getFundingType() == 'R' || fundingAddDto.getFundingType() == 'D')) {
            throw new ApiException(ExceptionEnum.ILLEGAL_FUNDINGTYPE_EXCEPTION);
        }

        Fundings savedFundings = fundingService.addFundingByEmail(email, fundingAddDto, fundingThumbnail, contentImage);
        FundingViewDto fundingViewDto = FundingViewDto.toDto(savedFundings);

        ResponseDto responseDto = ResponseDto.builder()
                .data(fundingViewDto)
                .build();

        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    // jwt 토큰으로 펀딩 수정
    @PatchMapping("/{fundingIdx}")
    public ResponseEntity<ResponseDto> modifyFundingByJwt(@AuthenticationPrincipal UserDetails userDetails,
                                                          @PathVariable int fundingIdx,
                                                          @Valid @RequestPart(value = "fundingAddDto") FundingAddDto fundingAddDto,
                                                          @RequestPart(value = "fundingThumbnail", required = false) MultipartFile fundingThumbnail,
                                                          @RequestPart(value = "contentImage", required = false) MultipartFile contentImage) {
        String email = userDetails.getUsername();

        if (!(fundingAddDto.getFundingType() == 'R' || fundingAddDto.getFundingType() == 'D')) {
            throw new ApiException(ExceptionEnum.ILLEGAL_FUNDINGTYPE_EXCEPTION);
        }

        Fundings modifiedFundings = fundingService.modifyFundingByFundingIdx(email, fundingIdx, fundingAddDto, fundingThumbnail, contentImage);
        FundingViewDto fundingViewDto = FundingViewDto.toDto(modifiedFundings);

        ResponseDto responseDto = ResponseDto.builder()
                .data(fundingViewDto)
                .build();

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    // jwt 토큰으로 펀딩 삭제
    @DeleteMapping("/{fundingIdx}")
    public ResponseEntity<ResponseDto> deleteFundingByJwt(@AuthenticationPrincipal UserDetails userDetails, @PathVariable int fundingIdx) {
        String email = userDetails.getUsername();

        Fundings deletedFundings = fundingService.deleteFundingByFundingIdx(email, fundingIdx);
        FundingViewDto fundingViewDto = FundingViewDto.toDto(deletedFundings);

        ResponseDto responseDto = ResponseDto.builder()
                .data(fundingViewDto)
                .build();

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    // ========= 댓글 관련 ===========
    // jwt 토큰에 해당하는 사용자가 작성한 모든 응원 댓글 조회
    @GetMapping("/my-comments")
    public ResponseEntity<ResponseDto> getCheerCommentListByJwt(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();

        List<MyCheerCommentViewDto> myCheerCommentViewDtoList =
                cheerCommentService.getCheerCommentListByEmail(email)
                        .stream()
                        .map(MyCheerCommentViewDto::toDto)  // 각 CheerComments 객체를 CheerCommentViewDto로 변환
                        .collect(Collectors.toList());  // List로 변환

        ResponseDto responseDto = ResponseDto.builder()
                .data(myCheerCommentViewDtoList)
                .build();

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    // fundingIdx에 해당하는 펀딩의 모든 댓글 조회
    @GetMapping("/{fundingIdx}/comments")
    public ResponseEntity<ResponseDto> getCheerCommentListByFundingIdx(@PathVariable int fundingIdx) {

        List<CheerCommentViewDto> cheerCommentViewDtoList =
                cheerCommentService.getCheerCommentListByFundingIdx(fundingIdx)
                        .stream()
                        .map(CheerCommentViewDto::toDto)  // 각 CheerComments 객체를 CheerCommentViewDto로 변환
                        .collect(Collectors.toList());  // List로 변환

        ResponseDto responseDto = ResponseDto.builder()
                .data(cheerCommentViewDtoList)
                .build();

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    // fundingIdx에 해당하는 펀딩의 commentIdx에 해당하는 댓글 조회
    @GetMapping("/{fundingIdx}/comments/{commentIdx}")
    public ResponseEntity<ResponseDto> getCheerCommentByFundingIdxAndCommentIdx(@PathVariable int fundingIdx, @PathVariable int commentIdx) {
        CheerComments savedCheerComments = cheerCommentService.getCheerCommentByFundingIdxAndCommentIdx(fundingIdx, commentIdx);
        CheerCommentViewDto cheerCommentViewDto = CheerCommentViewDto.toDto(savedCheerComments);

        ResponseDto responseDto = ResponseDto.builder()
                .data(cheerCommentViewDto)
                .build();

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    // jwt 토큰으로 댓글 작성
    @PostMapping("/{fundingIdx}/comments")
    public ResponseEntity<ResponseDto> addCheerCommentByJwt(@AuthenticationPrincipal UserDetails userDetails, @PathVariable int fundingIdx, @Valid @RequestBody CheerCommentAddDto cheerCommentAddDto) {
        String email = userDetails.getUsername();

        CheerComments savedCheerComments = cheerCommentService.addCheerCommentByEmail(email, fundingIdx, cheerCommentAddDto);
        CheerCommentViewDto cheerCommentViewDto = CheerCommentViewDto.toDto(savedCheerComments);

        ResponseDto responseDto = ResponseDto.builder()
                .data(cheerCommentViewDto)
                .build();

        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    // jwt 토큰으로 댓글 삭제
    @DeleteMapping("/{fundingIdx}/comments/{commentIdx}")
    public ResponseEntity<ResponseDto> deleteCheerCommentByJwt(@AuthenticationPrincipal UserDetails userDetails, @PathVariable int commentIdx, @PathVariable int fundingIdx) {
        String email = userDetails.getUsername();

        CheerComments deletedCheerComments = cheerCommentService.deleteCheerCommentByEmail(email, fundingIdx, commentIdx);
        CheerCommentViewDto cheerCommentViewDto = CheerCommentViewDto.toDto(deletedCheerComments);

        ResponseDto responseDto = ResponseDto.builder()
                .data(cheerCommentViewDto)
                .build();

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    // ========= 후기 관련 ===========
    // 특정 펀딩의 펀딩 후기 조회
    @GetMapping("/{fundingIdx}/review")
    public ResponseEntity<ResponseDto> getFundingReviewByJwt(@PathVariable int fundingIdx) {
        FundingReviewViewDto savedFundingReviewDto = fundingReviewService.getFundingReviewByFundingIdx(fundingIdx);

        ResponseDto responseDto = ResponseDto.builder()
                .data(savedFundingReviewDto)
                .build();

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    // jwt 토큰으로 펀딩 후기 추가
    @PostMapping("/{fundingIdx}/review")
    public ResponseEntity<ResponseDto> addFundingReviewByJwt(@AuthenticationPrincipal UserDetails userDetails,
                                                             @PathVariable int fundingIdx,
                                                             @Valid @RequestPart(value = "fundingReviewAddDto") FundingReviewAddDto fundingReviewAddDto,
                                                             @RequestPart(value = "contentImage", required = false) MultipartFile contentImage) {
        String email = userDetails.getUsername();

        FundingReviewViewDto savedFundingReviewDto = fundingReviewService.addFundingReviewByEmail(email, fundingIdx, fundingReviewAddDto, contentImage);

        ResponseDto responseDto = ResponseDto.builder()
                .data(savedFundingReviewDto)
                .build();

        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    // jwt 토큰으로 펀딩 후기 수정
    @PatchMapping("/{fundingIdx}/review")
    public ResponseEntity<ResponseDto> modifyFundingReviewByJwt(@AuthenticationPrincipal UserDetails userDetails,
                                                                @PathVariable int fundingIdx,
                                                                @Valid @RequestPart(value = "fundingReviewUpdateDto") FundingReviewUpdateDto fundingReviewUpdateDto,
                                                                @RequestPart(value = "contentImage", required = false) MultipartFile contentImage) {
        String email = userDetails.getUsername();

        FundingReviewViewDto modifiedFundingReviewDetailViewDto = fundingReviewService.modifyFundingReviewByEmail(email, fundingIdx, fundingReviewUpdateDto, contentImage);

        ResponseDto responseDto = ResponseDto.builder()
                .data(modifiedFundingReviewDetailViewDto)
                .build();

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping("/statistics/{fundingIdx}")
    public ResponseEntity<ResponseDto> getYearStatistics(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable int fundingIdx) {
        FundingStatisticsDto data = FundingStatisticsDto.builder()
                .fundingDayStatistic(fundingStatsService.getFundingDayStatisticByFundingIdx(userDetails.getUsername(), fundingIdx))
                .fundingParticipate(fundingStatsService.getFundingParticipateByFundingIdx(userDetails.getUsername(), fundingIdx))
                .fundingStatsByAgeAndGender(fundingStatsService.getFundingCountByAgeAndGender(userDetails.getUsername(), fundingIdx))
                .build();
        ResponseDto responseDto = ResponseDto.builder()
                .data(data)
                .build();
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

}

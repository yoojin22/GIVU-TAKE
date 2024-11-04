package com.accepted.givutake.gift.controller;

import com.accepted.givutake.gift.entity.Gifts;
import com.accepted.givutake.gift.model.*;
import com.accepted.givutake.gift.service.GiftService;
import com.accepted.givutake.gift.service.GiftStatsService;
import com.accepted.givutake.global.enumType.ActEnum;
import com.accepted.givutake.global.enumType.ContentTypeEnum;
import com.accepted.givutake.global.enumType.ExceptionEnum;
import com.accepted.givutake.global.exception.ApiException;
import com.accepted.givutake.global.model.CreateLogDto;
import com.accepted.givutake.global.model.ResponseDto;
import com.accepted.givutake.global.service.UserViewLogService;
import com.accepted.givutake.user.common.entity.Users;
import com.accepted.givutake.user.common.repository.UsersRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/gifts")
@CrossOrigin
public class GiftController {

    private final GiftService giftService;
    private final UserViewLogService userViewLogService;
    private final GiftStatsService giftStatsService;
    private final UsersRepository usersRepository;

    @GetMapping // 답례품 조회
    public ResponseEntity<ResponseDto> getGifts(
            @RequestParam(value = "corporationEmail", required = false)String corporationEmail,
            @RequestParam(value = "search", required = false)String search,
            @RequestParam(value = "categoryIdx", required = false)Integer categoryIdx,
            @RequestParam(value = "pageNo", defaultValue = "1")int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10")int pageSize) {
        List<GiftDto> gifts = giftService.getGifts(corporationEmail, search, categoryIdx,pageNo, pageSize);
        ResponseDto responseDto = ResponseDto.builder()
                .data(gifts)
                .build();
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping("/{giftIdx}") // 특정 답례품 조회
    public ResponseEntity<ResponseDto> getGift(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable int giftIdx) {
        GiftDto gift = giftService.getGift(giftIdx);
        if(userDetails != null) {
            CreateLogDto logDto = CreateLogDto.builder()
                    .contentType(ContentTypeEnum.GIFT)
                    .act(ActEnum.READ)
                    .build();
            userViewLogService.createLog(userDetails.getUsername(), logDto);
        }
        ResponseDto responseDto = ResponseDto.builder()
                .data(gift)
                .build();
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping("/recent")
    public ResponseEntity<ResponseDto> getRecentGifts(@AuthenticationPrincipal UserDetails userDetails) {
        List<GiftDto> data = giftService.getRecentGifts(userDetails.getUsername());
        ResponseDto responseDto = ResponseDto.builder()
                .data(data)
                .build();
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @PostMapping // 답례품 생성
    public ResponseEntity<ResponseDto> createGift(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestPart(value = "createGiftDto") CreateGiftDto request,
            @RequestPart(value = "thumbnailImage", required = false) MultipartFile thumbnailImage,
            @RequestPart(value = "contentImage", required = false) MultipartFile contentImage) {
        GrantedAuthority firstAuthority = userDetails.getAuthorities().stream().findFirst().orElseThrow(() -> new ApiException(ExceptionEnum.ACCESS_DENIED_EXCEPTION));
        if(!firstAuthority.getAuthority().equals("ROLE_CORPORATION"))throw new ApiException(ExceptionEnum.ACCESS_DENIED_EXCEPTION);
        System.out.println(firstAuthority.getAuthority());
        Gifts gift = giftService.createGift(userDetails.getUsername(), request, thumbnailImage, contentImage);
        CreateLogDto logDto = CreateLogDto.builder()
                .contentType(ContentTypeEnum.GIFT)
                .act(ActEnum.CREATE)
                .contentIdx(gift.getGiftIdx())
                .build();
        userViewLogService.createLog(userDetails.getUsername(), logDto);
        ResponseDto responseDto = ResponseDto.builder()
                .data(null)
                .build();
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @PatchMapping("/{giftsIdx}") // 답례품 수정
    public ResponseEntity<ResponseDto> updateGift(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable int giftsIdx,
            @Valid @RequestPart(value = "updateGiftDto") UpdateGiftDto request,
            @RequestPart(value = "thumbnailImage", required = false) MultipartFile thumbnailImage,
            @RequestPart(value = "contentImage", required = false) MultipartFile contentImage) {
        Gifts gift = giftService.updateGift(userDetails.getUsername(), giftsIdx, request, thumbnailImage, contentImage);
        CreateLogDto logDto = CreateLogDto.builder()
                .contentType(ContentTypeEnum.GIFT)
                .act(ActEnum.UPDATE)
                .contentIdx(gift.getGiftIdx())
                .build();
        userViewLogService.createLog(userDetails.getUsername(), logDto);
        ResponseDto responseDto = ResponseDto.builder()
                .data(null)
                .build();
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @DeleteMapping("/{giftsIdx}") // 답례품 삭제
    public ResponseEntity<ResponseDto> deleteGift(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable int giftsIdx)
    {
        GrantedAuthority firstAuthority = userDetails.getAuthorities().stream().findFirst().orElseThrow(() -> new ApiException(ExceptionEnum.ACCESS_DENIED_EXCEPTION));
        Gifts gift = giftService.deleteGift(firstAuthority.getAuthority(), userDetails.getUsername(), giftsIdx);
        CreateLogDto logDto = CreateLogDto.builder()
                .contentType(ContentTypeEnum.GIFT)
                .act(ActEnum.DELETE)
                .contentIdx(gift.getGiftIdx())
                .build();
        userViewLogService.createLog(userDetails.getUsername(), logDto);
        ResponseDto responseDto = ResponseDto.builder()
                .data(null)
                .build();
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping("/{giftIdx}/review") // 특정 답례품에 대한 후기 조회
    public ResponseEntity<ResponseDto> getGiftReviews(
            @PathVariable int giftIdx,
            @RequestParam(value = "pageNo", defaultValue = "1")int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10")int pageSize,
            @RequestParam(value = "isOrderLiked", defaultValue = "false")boolean isOrderLiked) {
        List<GiftReviewDto> reviews = giftService.getGiftReviews(giftIdx, isOrderLiked, pageNo, pageSize);
        ResponseDto responseDto = ResponseDto.builder()
                .data(reviews)
                .build();
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping("/review") // 특정 사용자가 작성한 후기 조회
    public ResponseEntity<ResponseDto> getUserReviews(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(value = "pageNo", defaultValue = "1")int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10")int pageSize,
            @RequestParam(value = "isOrderLiked", defaultValue = "false")boolean isOrderLiked){
        List<GiftReviewDto> reviews = giftService.getUserReviews(userDetails.getUsername(), isOrderLiked, pageNo, pageSize);
        ResponseDto responseDto = ResponseDto.builder()
                .data(reviews)
                .build();
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping("/review/{reviewIdx}") // 특정 후기 조회
    public ResponseEntity<ResponseDto> getUserReview(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable int reviewIdx){
        GiftReviewDto review = giftService.getUserReview(userDetails.getUsername(), reviewIdx);
        ResponseDto responseDto = ResponseDto.builder()
                .data(review)
                .build();
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping("/review/order/{orderIdx}")
    public ResponseEntity<ResponseDto> getUserReviewOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable long orderIdx){
        boolean data = giftService.IsWriteGiftReview(userDetails.getUsername(), orderIdx);
        ResponseDto responseDto = ResponseDto.builder()
                .data(data)
                .build();
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @PostMapping("/review") // 리뷰 작성
    public ResponseEntity<ResponseDto> createGiftReview(
            @AuthenticationPrincipal UserDetails userDetails ,
            @Valid @RequestPart(value = "createGiftReviewDto") CreateGiftReviewDto request,
            @RequestPart(value = "reviewImage", required = false) MultipartFile reviewImage) {
        giftService.createGiftReview(userDetails.getUsername(), request, reviewImage);
        ResponseDto responseDto = ResponseDto.builder()
                .data(null)
                .build();
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @PatchMapping("/review/{reviewIdx}") // 리뷰 수정
    public ResponseEntity<ResponseDto> updateGiftReview(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable int reviewIdx,
            @Valid @RequestPart(value = "updateGiftReviewDto") UpdateGiftReviewDto request,
            @RequestPart(value = "reviewImage", required = false) MultipartFile reviewImage) {
        giftService.updateGiftReviews(userDetails.getUsername(), reviewIdx, request, reviewImage);
        ResponseDto responseDto = ResponseDto.builder()
                .data(null)
                .build();
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @DeleteMapping("/review/{reviewIdx}") // 리뷰 삭제
    public ResponseEntity<ResponseDto> deleteGiftReview(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable int reviewIdx) {
        GrantedAuthority firstAuthority = userDetails.getAuthorities().stream().findFirst().orElseThrow(() -> new ApiException(ExceptionEnum.ACCESS_DENIED_EXCEPTION));
        giftService.deleteGiftReviews(firstAuthority.getAuthority(), userDetails.getUsername(), reviewIdx);
        ResponseDto responseDto = ResponseDto.builder()
                .data(null)
                .build();
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping("/review/{reviewIdx}/isLiked") // 리뷰 좋아요 추가
    public ResponseEntity<ResponseDto> isLiked(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable int reviewIdx) {
        boolean data = giftService.isLiked(userDetails.getUsername(), reviewIdx);
        ResponseDto responseDto = ResponseDto.builder()
                .data(data)
                .build();
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @PostMapping("/review/{reviewIdx}/insertLiked") // 리뷰 좋아요 추가
    public ResponseEntity<ResponseDto> insertLiked(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable int reviewIdx) {
        giftService.createLiked(userDetails.getUsername(), reviewIdx);
        ResponseDto responseDto = ResponseDto.builder()
                .data(null)
                .build();
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @DeleteMapping("/review/{reviewIdx}/deleteLiked") // 리뷰 좋아요 삭제
    public ResponseEntity<ResponseDto> deleteLiked(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable int reviewIdx) {
        giftService.deleteLiked(userDetails.getUsername(), reviewIdx);
        ResponseDto responseDto = ResponseDto.builder()
                .data(null)
                .build();
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping("/statistics")
    public ResponseEntity<ResponseDto> getYearStatistics(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) Integer giftIdx) {

        String email = userDetails.getUsername();

        Users user = usersRepository.findByEmail(email).orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_USER_WITH_EMAIL_EXCEPTION));

        GiftStatisticsDto data = GiftStatisticsDto
                .builder()
                .giftYearStatisticsDto(giftService.getGiftYearStatistics(email, giftIdx))
                .giftPurchaserDto(giftService.getGiftPurchaser(email, giftIdx))
                .giftPercentageDto(giftStatsService.getGiftPercentage(giftIdx, user.getUserIdx()))
                .build();

        ResponseDto responseDto = ResponseDto.builder()
                .data(data)
                .build();

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
}



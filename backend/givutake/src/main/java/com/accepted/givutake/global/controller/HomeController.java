package com.accepted.givutake.global.controller;

import com.accepted.givutake.funding.service.FundingParticipantService;
import com.accepted.givutake.funding.service.FundingService;
import com.accepted.givutake.gift.service.GiftService;
import com.accepted.givutake.global.model.HomeDto;
import com.accepted.givutake.global.model.ResponseDto;
import com.accepted.givutake.payment.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/home")
@Slf4j
public class HomeController {

    private final OrderService orderService;
    private final GiftService giftService;
    private final FundingParticipantService fundingParticipantService;
    private final FundingService fundingService;

    @GetMapping("/price")
    public ResponseEntity<ResponseDto> calculateTotalPrice() {
        long fundingSum = fundingParticipantService.calculateTotalFundingFee();
        long giftSum = orderService.calculateTotalOrderPrice();

        long sum = fundingSum + giftSum;

        Map<String, Long> map = new HashMap<>();
        map.put("price", sum);

        ResponseDto responseDto = ResponseDto.builder()
                .data(map)
                .build();

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<ResponseDto> home(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();

        HomeDto data = HomeDto.builder()
                .top10Gifts(giftService.getTop10Gifts())
                .recentGifts(giftService.getRecentGifts(email))
                .deadlineImminentFundings(fundingService.getDeadlineImminentFundings())
                .build();

        ResponseDto responseDto = ResponseDto.builder()
                .data(data)
                .build();

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

}

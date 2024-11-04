package com.accepted.givutake.payment.controller;

import com.accepted.givutake.global.model.ResponseDto;
import com.accepted.givutake.payment.entity.Orders;
import com.accepted.givutake.payment.model.*;
import com.accepted.givutake.payment.service.KaKaoPayService;
import com.accepted.givutake.payment.service.OrderService;
import com.accepted.givutake.payment.service.ParticipantService;
import com.accepted.givutake.payment.utils.SessionUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/purchases")
@CrossOrigin
@Slf4j
public class OrderController {

    private final OrderService orderService;
    private final KaKaoPayService kaKaoPayService;
    private final ParticipantService participantService;

    @GetMapping
    public ResponseEntity<ResponseDto> getOrders(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(value = "pageNo", defaultValue = "1")int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10")int pageSize) {
        List<OrderDto> orders = orderService.getOrders(userDetails.getUsername(), pageNo, pageSize);
        ResponseDto responseDto = ResponseDto.builder()
                .data(orders)
                .build();
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping("/{orderIdx}")
    public ResponseEntity<ResponseDto> getOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable int orderIdx){
        OrderDto order = orderService.getOrder(userDetails.getUsername(), orderIdx);
        ResponseDto responseDto = ResponseDto.builder()
                .data(order)
                .build();
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping("/completed")
    public String payCompleted(
            @RequestParam("email") String email,
            @RequestParam("pg_token") String pgToken,
            @RequestParam("orderIdx") int orderIdx,
            @RequestParam("type") String type
            ) {

        String tid = SessionUtils.getStringAttributeValue("tid");
        log.info("결제승인 요청을 인증하는 토큰: {}", pgToken);
        log.info("결제 고유번호: {}", tid);

        ApproveResponse approveResponse = kaKaoPayService.payApprove(email, orderIdx,tid, pgToken, type);

        return "redirect:/payment/success";
    }

    @GetMapping("/cancel")
    public String payCancel(@RequestParam("email") String email,
                            @RequestParam("orderIdx") int orderIdx,
                            @RequestParam("type") String type) {
        if(type.equals("Gift"))orderService.deleteOrder(email, orderIdx);
        else participantService.deleteParticipant(email, orderIdx);
        return "취소";
    }

    @GetMapping("/fail")
    public String payFail(@RequestParam("email") String email,
                          @RequestParam("orderIdx") int orderIdx,
                          @RequestParam("type") String type) {
        if(type.equals("Gift"))orderService.deleteOrder(email, orderIdx);
        else participantService.deleteParticipant(email, orderIdx);
        return "실패";
    }

    @PostMapping
    public @ResponseBody ReadyResponse createOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateOrderDto request) {
        Orders order = orderService.createOrder(userDetails.getUsername(), request);
        if(request.getPaymentMethod().equals("KAKAO")){
            ReadyResponse readyResponse = kaKaoPayService.payGiftReady(userDetails.getUsername(), order.getOrderIdx(),request);
            SessionUtils.addAttribute("tid", readyResponse.getTid());
            readyResponse.setStatus("success");
            orderService.updateAmount(order.getGift().getGiftIdx(), order.getAmount());
            return readyResponse;
        }else{
            orderService.updateAmount(order.getGift().getGiftIdx(), order.getAmount());
            return ReadyResponse.builder()
                    .status("success")
                    .build();
        }
    }

    @PatchMapping("/{orderIdx}")
    public ResponseEntity<ResponseDto> updateOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable int orderIdx,
            @Valid @RequestBody UpdateOrderDto request){
        orderService.updateOrder(userDetails.getUsername(), orderIdx, request);
        ResponseDto responseDto = ResponseDto.builder()
                .data(null)
                .build();
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping("/statistics/{giftIdx}")
    public ResponseEntity<ResponseDto> getOrderStatistics(@PathVariable int giftIdx){
        int data = orderService.countGift(giftIdx);
        ResponseDto responseDto = ResponseDto.builder()
                .data(data)
                .build();
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
}

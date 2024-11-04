package com.accepted.givutake.payment.service;

import com.accepted.givutake.gift.entity.Gifts;
import com.accepted.givutake.gift.enumType.DeliveryStatus;
import com.accepted.givutake.gift.repository.GiftRepository;
import com.accepted.givutake.gift.service.GiftService;
import com.accepted.givutake.global.enumType.ExceptionEnum;
import com.accepted.givutake.global.exception.ApiException;
import com.accepted.givutake.payment.entity.Orders;
import com.accepted.givutake.payment.model.CreateOrderDto;
import com.accepted.givutake.payment.model.OrderDto;
import com.accepted.givutake.payment.model.UpdateOrderDto;
import com.accepted.givutake.payment.repository.OrderRepository;
import com.accepted.givutake.user.common.entity.Users;
import com.accepted.givutake.user.common.model.UserDto;
import com.accepted.givutake.user.common.repository.UsersRepository;
import com.accepted.givutake.user.common.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final UsersRepository userRepository;
    private final GiftRepository giftRepository;
    private final UserService userService;
    private final GiftService giftService;

    public Orders createOrder(String email, CreateOrderDto request){
        Users user = userRepository.findByEmail(email).orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_USER_WITH_EMAIL_EXCEPTION));
        Gifts gift = giftRepository.findById(request.getGiftIdx()).orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_GIFT_EXCEPTION));
        Orders newOrder = Orders.builder()
                .users(user)
                .gift(gift)
                .paymentMethod(request.getPaymentMethod())
                .amount(request.getAmount())
                .price(request.getAmount()*gift.getPrice())
                .cardNumber(request.getCardNumber())
                .status(DeliveryStatus.PROCESSED)
                .build();
        return orderRepository.save(newOrder);
    }

    public List<OrderDto> getOrders(String email, int pageNo, int pageSize){
        Pageable pageable = PageRequest.of(pageNo-1, pageSize, Sort.by(Sort.Direction.DESC, "createdDate"));

        Users user = userRepository.findByEmail(email).orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_USER_WITH_EMAIL_EXCEPTION));

        Page<Orders> orderList = orderRepository.findByUsers(user, pageable);

        return orderList.map(order -> OrderDto.builder()
                .orderIdx(order.getOrderIdx())
                .userIdx(user.getUserIdx())
                .regionName(order.getGift().getCorporations().getRegion().getSigungu())
                .giftIdx(order.getGift().getGiftIdx())
                .giftName(order.getGift().getGiftName())
                .giftThumbnail(order.getGift().getGiftThumbnail())
                .paymentMethod(order.getPaymentMethod())
                .amount(order.getAmount())
                .price(order.getPrice())
                .status(order.getStatus())
                .isWrite(giftService.IsWriteGiftReview(email,order.getOrderIdx()))
                .createdDate(order.getCreatedDate())
                .build()
        ).toList();
    }

    // 특정 일자의 자신의 답례품 구매 내역 가져오기
    public List<Orders> getOrdersCreatedDateBetweenByEmail(String email, LocalDate startDate, LocalDate endDate) {
        // 1. 유저 정보 DB에서 조회
        UserDto savedUserDto = userService.getUserByEmail(email);
        Users savedUsers = savedUserDto.toEntity();

        // 2. 특정 일자의 구매 내역 가져오기
        LocalDateTime startDateTime;
        LocalDateTime endDateTime;

        // startDate와 endDate가 null이면 모든 데이터 조회
        if (startDate == null && endDate == null) {
            return orderRepository.findByUsers(savedUsers);
        }
        // 시작일이 null일 경우 endDate이전의 모든 데이터 조회
        else if (startDate == null) {
            endDateTime = endDate.atTime(23, 59, 59);
            return orderRepository.findByUsersAndCreatedDateBefore(savedUsers, endDateTime);
        }
        // 종료일이 null일 경우 startDate 이후의 모든 데이터 조회
        else if (endDate == null) {
            startDateTime = startDate.atStartOfDay();
            return orderRepository.findByUsersAndCreatedDateAfter(savedUsers, startDateTime);
        }
        else {
            startDateTime = startDate.atStartOfDay();
            endDateTime = endDate.atTime(23, 59, 59);
            return orderRepository.findByUsersAndCreatedDateBetween(savedUsers, startDateTime, endDateTime);
        }
    }

    public OrderDto getOrder(String email, long orderIdx){
        Orders order = orderRepository.findById(orderIdx).orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_ORDER_EXCEPTION));

        if(!order.getUsers().getEmail().equals(email)){
            throw new ApiException(ExceptionEnum.ACCESS_DENIED_EXCEPTION);
        }

        return OrderDto.builder()
                .orderIdx(order.getOrderIdx())
                .userIdx(order.getUsers().getUserIdx())
                .regionName(order.getGift().getCorporations().getRegion().getSigungu())
                .giftIdx(order.getGift().getGiftIdx())
                .giftName(order.getGift().getGiftName())
                .giftThumbnail(order.getGift().getGiftThumbnail())
                .paymentMethod(order.getPaymentMethod())
                .amount(order.getAmount())
                .price(order.getPrice())
                .status(order.getStatus())
                .isWrite(giftService.IsWriteGiftReview(email,order.getOrderIdx()))
                .createdDate(order.getCreatedDate())
                .build();
    }

    public void updateOrder(String email, long orderIdx, UpdateOrderDto request){
        Orders order = orderRepository.findById(orderIdx).orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_ORDER_EXCEPTION));

        if(!order.getGift().getCorporations().getEmail().equals(email)){ // 지자체만 배송업데이트 가능
            throw new ApiException(ExceptionEnum.ACCESS_DENIED_EXCEPTION);
        }

        order.setStatus(request.getStatus());
        orderRepository.save(order);
    }

    public void updateAmount(int giftIdx, int amount){
        Gifts gift = giftRepository.findById(giftIdx).orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_GIFT_EXCEPTION));

        gift.setAmount(gift.getAmount() + amount);

        giftRepository.save(gift);
    }

    public void deleteOrder(String email, long orderIdx){
        Orders order = orderRepository.findById(orderIdx).orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_ORDER_EXCEPTION));

        if(!order.getUsers().getEmail().equals(email)){
            throw new ApiException(ExceptionEnum.ACCESS_DENIED_EXCEPTION);
        }

        orderRepository.delete(order);
    }

    public int countGift(int giftIdx){
        Gifts gift = giftRepository.findById(giftIdx).orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_GIFT_EXCEPTION));
        return orderRepository.countByGift(gift);
    }

    public long calculateTotalOrderPrice() {
        return Optional.ofNullable(orderRepository.getTotalOrderPrice()).orElse(0L);
    }

    public long calculateTotalOrderPriceByEmail(String email) {
        // 1. 유저 조회
        UserDto savedUserDto = userService.getUserByEmail(email);

        // 2. 사용자가 구매한 모든 답례품의 총금액 조회
        Long sum = orderRepository.sumPriceByUserIdx(savedUserDto.getUserIdx());

        if (sum == null) {
            return 0L;
        }

        return sum;
    }

}

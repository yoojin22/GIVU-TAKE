package com.accepted.givutake.cart.service;

import com.accepted.givutake.cart.entity.Carts;
import com.accepted.givutake.cart.model.CartDto;
import com.accepted.givutake.cart.model.CreateCartDto;
import com.accepted.givutake.cart.model.UpdateCartDto;
import com.accepted.givutake.cart.repository.CartRepository;
import com.accepted.givutake.gift.entity.Gifts;
import com.accepted.givutake.gift.repository.GiftRepository;
import com.accepted.givutake.global.enumType.ExceptionEnum;
import com.accepted.givutake.global.exception.ApiException;
import com.accepted.givutake.user.common.entity.Users;
import com.accepted.givutake.user.common.model.UserDto;
import com.accepted.givutake.user.common.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final GiftRepository giftRepository;
    private final UserService userService;

    public void createCart(String email, CreateCartDto request) {
        Gifts gift = giftRepository.findById(request.getGiftIdx()).orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_GIFT_EXCEPTION));
        UserDto savedUserDto = userService.getUserByEmail(email);
        Users user = savedUserDto.toEntity();
        Carts newCart = Carts.builder()
                .gifts(gift)
                .users(user)
                .amount(request.getAmount())
                .build();
        cartRepository.save(newCart);
    }

    public List<CartDto> getCartList(String email, int pageNo, int pageSize){
        Pageable pageable = PageRequest.of(pageNo-1, pageSize, Sort.by(Sort.Direction.DESC, "createdDate"));

        UserDto savedUserDto = userService.getUserByEmail(email);
        Users user = savedUserDto.toEntity();

        Page<Carts> cartList = cartRepository.findByUsers(user, pageable);

        return cartList.map(cart -> CartDto.builder()
                .cartIdx(cart.getCartIdx())
                .giftIdx(cart.getGifts().getGiftIdx())
                .giftName(cart.getGifts().getGiftName())
                .giftThumbnail(cart.getGifts().getGiftThumbnail())
                .sido(cart.getGifts().getCorporations().getRegion().getSido())
                .sigungu(cart.getGifts().getCorporations().getRegion().getSigungu())
                .userIdx(user.getUserIdx())
                .amount(cart.getAmount())
                .price(cart.getAmount()*cart.getGifts().getPrice())
                .build()
        ).toList();
    }

    public void updateCart(String email, int cartIdx, UpdateCartDto request) {
        Carts cart = cartRepository.findById(cartIdx).orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_SHOPPING_CART_EXCEPTION));
        if(!cart.getUsers().getEmail().equals(email)){
            throw new ApiException(ExceptionEnum.ACCESS_DENIED_EXCEPTION);
        }
        cart.setAmount(request.getAmount());
        cartRepository.save(cart);
    }

    public void deleteCart(String email, int cartIdx) {
        Carts cart = cartRepository.findById(cartIdx).orElseThrow();
        if(!cart.getUsers().getEmail().equals(email)){
            throw new ApiException(ExceptionEnum.ACCESS_DENIED_EXCEPTION);
        }
        cartRepository.delete(cart);
    }
}

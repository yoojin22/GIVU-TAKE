package com.accepted.givutake.cart.controller;

import com.accepted.givutake.cart.model.CartDto;
import com.accepted.givutake.cart.model.CreateCartDto;
import com.accepted.givutake.cart.model.UpdateCartDto;
import com.accepted.givutake.cart.service.CartService;
import com.accepted.givutake.global.model.ResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/shopping-cart")
@CrossOrigin
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<ResponseDto> getCart(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(value = "pageNo", defaultValue = "1")int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10")int pageSize) {
        List<CartDto> cartsList = cartService.getCartList(userDetails.getUsername(), pageNo, pageSize);
        ResponseDto responseDto = ResponseDto.builder()
                .data(cartsList)
                .build();
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ResponseDto> createCart(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateCartDto request){
        cartService.createCart(userDetails.getUsername(), request);
        ResponseDto responseDto = ResponseDto.builder()
                .data(null)
                .build();
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @PatchMapping("/{cartIdx}")
    public ResponseEntity<ResponseDto> updateCart(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable int cartIdx,
            @Valid @RequestBody UpdateCartDto request) {
        cartService.updateCart(userDetails.getUsername(), cartIdx, request);
        ResponseDto responseDto = ResponseDto.builder()
                .data(null)
                .build();
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @DeleteMapping("/{cartIdx}")
    public ResponseEntity<ResponseDto> deleteCart(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable int cartIdx) {
        cartService.deleteCart(userDetails.getUsername(), cartIdx);
        ResponseDto responseDto = ResponseDto.builder()
                .data(null)
                .build();
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
}

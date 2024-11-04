package com.accepted.givutake.user.common.controller;

import com.accepted.givutake.global.model.ResponseDto;
import com.accepted.givutake.user.common.JwtTokenProvider;
import com.accepted.givutake.user.common.model.JwtTokenDto;
import com.accepted.givutake.user.common.model.LoginDto;
import com.accepted.givutake.user.common.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    // 로그인
    @PostMapping
    public ResponseEntity<ResponseDto> login(@Valid @RequestBody LoginDto loginDto) {
        JwtTokenDto jwtToken = authService.login(loginDto);

        ResponseDto responseDto = ResponseDto.builder()
                .data(jwtToken)
                .build();

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    // refresh 토큰으로 acccess token, refresh token 재발급
    @GetMapping("/reissue")
    public ResponseEntity<ResponseDto> reissue(HttpServletRequest httpRequest) {
        // Request Header에서 토큰 정보 추출
        String token = jwtTokenProvider.resolveTokenFromRequestHeader(httpRequest);

        JwtTokenDto newJwtToken = authService.reissueToken(token);
        ResponseDto responseDto = ResponseDto.builder()
                .data(newJwtToken)
                .build();

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
}

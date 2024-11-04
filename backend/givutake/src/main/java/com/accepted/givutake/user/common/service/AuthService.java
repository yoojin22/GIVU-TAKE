package com.accepted.givutake.user.common.service;

import com.accepted.givutake.global.enumType.ExceptionEnum;
import com.accepted.givutake.global.exception.ApiException;
import com.accepted.givutake.user.common.JwtTokenProvider;
import com.accepted.givutake.user.common.entity.RefreshToken;
import com.accepted.givutake.user.common.model.CustomUserDetailsDto;
import com.accepted.givutake.user.common.model.JwtTokenDto;
import com.accepted.givutake.user.common.model.LoginDto;
import com.accepted.givutake.user.common.model.UserDto;
import com.accepted.givutake.user.common.repository.RefreshTokenRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final UserService userService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public JwtTokenDto login(LoginDto loginDto) {
        String email = loginDto.getEmail();
        String password = loginDto.getPassword();

        // 1. `email + password 를 기반으로 Authentication 객체 생성
        // 이때 authentication 은 인증 여부를 확인하는 authenticated 값이 false
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, password);
        
        // 2. 실제 검증. authenticate() 메서드를 통해 요청된 User 에 대한 검증 진행
        // authenticate 메서드가 실행될 때 CustomUserDetailsService 에서 만든 loadUserByUsername 메서드 실행
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 3. 인증 정보를 기반으로 JWT 토큰 생성 및 리턴
        return jwtTokenProvider.generateToken(authentication);
    }

    // refresh 토큰으로 acccess token, refresh token 재발급
    public JwtTokenDto reissueToken(String refreshToken) {
        // 1. 토큰 복호화
        Claims claims = jwtTokenProvider.parseClaims(refreshToken);
        String email = claims.getSubject();

        // 2. redis에 저장된 refresh 토큰과 맞는지 검증
        this.verifyRefreshToken(refreshToken, email);

        // 3. db에서 email에 맞는 user 꺼내오기
        UserDto userDto = userService.getUserByEmail(email);

        // 4. UserDetails 객체를 만들어서 Authentication 생성
        UserDetails principal = new CustomUserDetailsDto(userDto);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, "", principal.getAuthorities());

        // JWT 토큰 발급
        return jwtTokenProvider.generateToken(authentication);
    }

    // redis에 저장된 refresh 토큰과 맞는지 검증
    public void verifyRefreshToken(String refreshToken, String email) {
        // 2. redis에 있는 refresh 토큰 꺼내오기
        Optional<RefreshToken> existingRefreshTokenOptional = refreshTokenRepository.findByEmail(email);

        // redis에 refresh 토큰이 존재하는 경우
        if (existingRefreshTokenOptional.isPresent()) {
            // redis에 있는 토큰과 같은지 비교
            String redisRefreshToken = existingRefreshTokenOptional.get().getRefreshToken();

            // redis에 있는 토큰과 들어온 토큰이 같지 않은 경우 예외 발생
            if (!redisRefreshToken.equals(refreshToken)) {
                throw new ApiException(ExceptionEnum.REFRESHTOKEN_MISMATCH_EXCEPTION);
            }
        }
        // redis에 refresh 토큰이 존재하지 않는 경우 예외 발생
        else {
            throw new ApiException(ExceptionEnum.NOT_FOUND_REFRESHTOKEN_EXCEPTION);
        }
    }

}

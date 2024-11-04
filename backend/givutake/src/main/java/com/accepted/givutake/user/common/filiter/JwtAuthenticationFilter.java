package com.accepted.givutake.user.common.filiter;

import com.accepted.givutake.global.enumType.ExceptionEnum;
import com.accepted.givutake.global.exception.JwtAuthenticationException;
import com.accepted.givutake.global.model.ExceptionDto;
import com.accepted.givutake.user.common.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // 1. Request Header에서 JWT 토큰 추출
        String token = jwtTokenProvider.resolveTokenFromRequestHeader((HttpServletRequest) request);

        // 2. validateToken으로 토큰 유효성 검사
        if (token != null) {
            try {
                jwtTokenProvider.validateToken(token);
            } catch (JwtAuthenticationException e) {
                ExceptionEnum exceptionEnum = ExceptionEnum.valueOf(e.getMessage());
                handleException((HttpServletResponse) response, exceptionEnum);
                return; // 필터 체인 진행을 중단하고 응답을 전송
            }
            // 토큰이 유효할 경우 토큰에서 Authentication 객체를 가지고 와서 SecurityContext에 저장
            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 토큰 정보가 없거나 유효하지 않다면 다음 체인으로 이동
        chain.doFilter(request, response);
    }

    private void handleException(HttpServletResponse response, ExceptionEnum exceptionEnum) throws IOException {
        // ExceptionDto 객체 생성
        ExceptionDto exceptionDto = ExceptionDto.builder()
                .code(exceptionEnum.getCode())
                .message(exceptionEnum.getMessage())
                .build();

        // JSON 형태로 응답 작성
        String responseBody = new ObjectMapper().writeValueAsString(exceptionDto);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(responseBody);
    }

}

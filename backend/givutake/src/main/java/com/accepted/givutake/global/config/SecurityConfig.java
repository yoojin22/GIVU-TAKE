package com.accepted.givutake.global.config;

import com.accepted.givutake.global.handler.CustomAccessDeniedHandler;
import com.accepted.givutake.user.common.JwtTokenProvider;
import com.accepted.givutake.user.common.filiter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // REST API이므로 basic auth 및 csrf 보안을 사용하지 않음
                .httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                // form 로그인 방식 disable
                .formLogin(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // 경로별 인가 작업
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/",
                                "/api/auth",
                                "/api/swagger-ui/**",
                                "/api/v3/api-docs/**",
                                "/api/swagger-resources/**"
                        ).permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/government-fundings/my-fundings").hasRole("CORPORATION")
                        .requestMatchers(HttpMethod.GET, "/api/government-fundings/*/review",
                                "/api/government-fundings/*/comments",
                                "/api/government-fundings/*/comments/*",
                                "/api/regions/sido",
                                "/api/regions/sigungu",
                                "/api/government-fundings",
                                "/api/government-fundings/*",
                                "/api/experience-village",
                                "/api/purchases/completed",
                                "/api/purchases/cancel",
                                "/api/purchases/fail",
                                "/api/home/price",
                                "/api/gifts/**"
                        ).permitAll()
                        .requestMatchers(HttpMethod.GET,
                                "/api/gifts/review/**"
                        ).authenticated()
                        .requestMatchers(HttpMethod.POST,"/api/users",
                                "/api/admin",
                                "/api/users/password/code",
                                "/api/users/password/code/verification").permitAll()
                        .requestMatchers(HttpMethod.PATCH,"/api/users/password").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/users/corporations/certificates").hasAnyRole("CORPORATIONYET", "CORPORATION")
                        .requestMatchers(HttpMethod.POST,
                                "/api/government-fundings",
                                "/api/government-fundings/*/review")
                                .hasRole("CORPORATION")
                        .requestMatchers(HttpMethod.PATCH, "/api/government-fundings/*/review",
                                "/api/government-fundings/*")
                                .hasRole("CORPORATION")
                        .requestMatchers(HttpMethod.DELETE, "/api/government-fundings/*",
                                "/api/users").hasRole("CORPORATION")
                        .requestMatchers(HttpMethod.DELETE, "api/users").hasRole("CORPORATIONYET")
                        .requestMatchers("/api/users/client/**").hasRole("CLIENT")
                        .requestMatchers(HttpMethod.POST, "/api/participants").hasRole("CLIENT")
                        .requestMatchers(HttpMethod.DELETE,"/api/users").hasRole("CLIENT")
                        // 이 밖에 모든 요청에 대해서 인증을 필요로 한다는 설정
                        .anyRequest().authenticated()
                )
                // JWT를 사용하기 때문에 세션을 사용하지 않음
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 필터 추가 및 순서 설정
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
                // 예외 처리 설정
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        // 인증되지 않은 사용자에 대한 처리
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                        // 인가되지 않은 사용자에 대한 처리
                        .accessDeniedHandler(customAccessDeniedHandler)
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt Encoder 사용
        return new BCryptPasswordEncoder();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true);
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
//        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
        configuration.setAllowedOriginPatterns(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}

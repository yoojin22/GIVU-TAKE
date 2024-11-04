package com.accepted.givutake.user.common.repository;

import com.accepted.givutake.user.common.entity.RefreshToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Slf4j
@Repository
public class RefreshTokenRepository {

    private final RedisTemplate<String, String> redisTemplate;

    public RefreshTokenRepository(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // 이메일을 Key로, 리프레시 토큰을 Value로 저장
    public void save(RefreshToken refreshToken) {
        redisTemplate.opsForValue().set("RefreshToken:" + refreshToken.getEmail(), refreshToken.getRefreshToken(), Duration.ofSeconds(refreshToken.getTtl()));
    }

    // 이메일로 리프레시 토큰 조회
    public Optional<RefreshToken> findByEmail(String email) {
        String token = redisTemplate.opsForValue().get("RefreshToken:" + email);

        if (token != null) {
            return Optional.of(RefreshToken.builder()
                    .email(email)
                    .refreshToken(token)
                    .build());
        }
        return Optional.empty();
    }

    // 이메일로 저장된 인증 코드 삭제
    public void deleteByEmail(String email) {
        redisTemplate.delete("RefreshToken:" + email);
    }
}

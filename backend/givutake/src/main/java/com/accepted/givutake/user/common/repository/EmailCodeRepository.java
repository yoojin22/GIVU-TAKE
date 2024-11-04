package com.accepted.givutake.user.common.repository;

import com.accepted.givutake.user.common.entity.EmailCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Slf4j
@Repository
public class EmailCodeRepository {

    private final RedisTemplate<String, String> redisTemplate;

    public EmailCodeRepository(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // 이메일을 Key로, 인증 코드를 Value로 저장
    public void save(EmailCode emailCode) {
        redisTemplate.opsForValue().set("EmailCode:" + emailCode.getEmail(), emailCode.getCode(), Duration.ofSeconds(emailCode.getTtl()));
    }

    // 이메일로 인증 코드 조회
    public Optional<EmailCode> findByEmail(String email) {
        String code = redisTemplate.opsForValue().get("EmailCode:" + email);

        if (code != null) {
            return Optional.of(EmailCode.builder()
                .email(email)
                .code(code)
                .build());
        }
        return Optional.empty();
    }

    // 이메일로 저장된 인증 코드 삭제
    public void deleteByEmail(String email) {
        redisTemplate.delete("EmailCode:" + email);
    }
}

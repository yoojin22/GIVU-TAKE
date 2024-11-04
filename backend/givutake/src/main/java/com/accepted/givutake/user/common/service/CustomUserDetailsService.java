package com.accepted.givutake.user.common.service;

import com.accepted.givutake.global.enumType.ExceptionEnum;
import com.accepted.givutake.global.exception.ApiException;
import com.accepted.givutake.user.common.entity.Users;
import com.accepted.givutake.user.common.model.CustomUserDetailsDto;
import com.accepted.givutake.user.common.model.UserDto;
import com.accepted.givutake.user.common.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsersRepository userRepository;

    // email로 사용자 엔티티 조회
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Users> existingUserOptional = userRepository.findByEmail(username);

        if (existingUserOptional.isEmpty()) {
            throw new ApiException(ExceptionEnum.NOT_FOUND_USER_WITH_EMAIL_EXCEPTION);
        }

        Users user = existingUserOptional.get();

        // 이미 탈퇴한 회원이라면 오류 발생
        if (user.isWithdraw()) {
            throw new ApiException(ExceptionEnum.USER_ALREADY_WITHDRAWN_EXCEPTION);
        }

        return new CustomUserDetailsDto(UserDto.toDto(user));
    }

}

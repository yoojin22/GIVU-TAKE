package com.accepted.givutake.global.service;

import com.accepted.givutake.global.entity.UserViewLogs;
import com.accepted.givutake.global.model.CreateLogDto;
import com.accepted.givutake.global.repository.UserViewLogRepository;
import com.accepted.givutake.user.common.entity.Users;
import com.accepted.givutake.user.common.model.UserDto;
import com.accepted.givutake.user.common.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class UserViewLogService {

    private final UserViewLogRepository userViewLogRepository;
    private final UserService userService;

    public void createLog(String email, CreateLogDto request){
        UserDto savedUserDto = userService.getUserByEmail(email);
        Users user = savedUserDto.toEntity();
        UserViewLogs log = UserViewLogs.builder()
                .users(user)
                .contentType(request.getContentType())
                .actEnum(request.getAct())
                .contentIdx(request.getContentIdx())
                .build();
        userViewLogRepository.save(log);
    }
}

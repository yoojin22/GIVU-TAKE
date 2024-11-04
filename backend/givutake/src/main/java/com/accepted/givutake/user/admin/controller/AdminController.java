package com.accepted.givutake.user.admin.controller;

import com.accepted.givutake.global.model.ResponseDto;
import com.accepted.givutake.user.admin.model.AdminCorporationViewDto;
import com.accepted.givutake.user.admin.model.AdminSignUpDto;
import com.accepted.givutake.user.admin.model.AdminUserViewDto;
import com.accepted.givutake.user.admin.service.AdminService;
import com.accepted.givutake.user.common.entity.Users;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    // 관리자 회원가입
    @PostMapping
    public ResponseEntity<ResponseDto> signUp(@RequestPart(value = "adminSignUpDto") @Valid AdminSignUpDto adminSignUpDto,
                                              @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {
        Users savedUsers = adminService.signUp(adminSignUpDto, profileImage);
        AdminUserViewDto adminUserViewDto = AdminUserViewDto.toDto(savedUsers);

        ResponseDto responseDto = ResponseDto.builder()
                .data(adminUserViewDto)
                .build();

        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    // 조건에 맞는 수혜자 정보 조회
    @GetMapping("/corporations")
    public ResponseEntity<ResponseDto> getCorporations(@RequestParam(required = false) Character isApproved,
                                                       @RequestParam(required = false, defaultValue = "0") int pageNo,
                                                       @RequestParam(required = false, defaultValue = "10") int pageSize) {

        List<AdminCorporationViewDto> adminCorporationViewDtoList = adminService.getCorporation(isApproved, pageNo, pageSize)
                .stream()
                .map(AdminCorporationViewDto::toDto)
                .collect(Collectors.toList());

        ResponseDto responseDto = ResponseDto.builder()
                .data(adminCorporationViewDtoList)
                .build();

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    // 수혜자 자격 변경
    @PatchMapping("/corporations/{email}/roles")
    public ResponseEntity<ResponseDto> updateCorporationRole(@PathVariable String email, @RequestParam Character isApproved) {
        Users savedUsers = adminService.updateCorporationRole(email, isApproved);
        AdminCorporationViewDto adminCorporationViewDto = AdminCorporationViewDto.toDto(savedUsers);

        ResponseDto responseDto = ResponseDto.builder()
                .data(adminCorporationViewDto)
                .build();

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

}

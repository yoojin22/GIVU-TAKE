package com.accepted.givutake.user.common.controller;

import com.accepted.givutake.global.enumType.ExceptionEnum;
import com.accepted.givutake.global.exception.ApiException;
import com.accepted.givutake.global.model.ResponseDto;
import com.accepted.givutake.user.admin.model.AdminDetailViewDto;
import com.accepted.givutake.user.client.model.AddressSignUpDto;
import com.accepted.givutake.user.client.model.ClientViewDto;
import com.accepted.givutake.user.common.enumType.Roles;
import com.accepted.givutake.user.common.model.*;
import com.accepted.givutake.user.common.service.UserService;
import com.accepted.givutake.user.corporation.model.CorporationDetailViewDto;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class  UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<ResponseDto> emailSignUp(
            @RequestPart(value = "signUpDto") @Valid SignUpDto signUpDto,
            @RequestPart(value = "addressSignUpDto", required = false) AddressSignUpDto addressSignUpDto,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {

        userService.emailSignUp(signUpDto, addressSignUpDto, profileImage);

        ResponseDto responseDto = ResponseDto.builder()
                .data(null)
                .build();

        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    // JWT 토큰으로 회원 정보 조회
    @GetMapping
    public ResponseEntity<ResponseDto> getUserByToken(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        UserDto savedUserDto = userService.getUserByEmail(email);

        ResponseDto responseDto = ResponseDto.builder()
                .data(null)
                .build();

        if (savedUserDto.getRoles() == Roles.ROLE_CLIENT) {
            ClientViewDto clientViewDto = savedUserDto.toClientViewDto();
            responseDto.setData(clientViewDto);
        }
        else if (savedUserDto.getRoles() == Roles.ROLE_CORPORATION || savedUserDto.getRoles() == Roles.ROLE_CORPORATIONYET) {
            CorporationDetailViewDto corporationViewDto = savedUserDto.toCorporationViewDto();
            responseDto.setData(corporationViewDto);
        }
        else if (savedUserDto.getRoles() == Roles.ROLE_ADMIN) {
            AdminDetailViewDto adminDetailViewDto = savedUserDto.toAdminViewDto();
            responseDto.setData(adminDetailViewDto);
        }
        else {
            throw new ApiException(ExceptionEnum.ACCESS_DENIED_EXCEPTION);
        }

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    // JWT 토큰으로 회원 정보 수정
    @PatchMapping
    public ResponseEntity<ResponseDto> modifyUserByToken(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestPart(value = "modifyUserDto") ModifyUserDto modifyUserDto,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {
        String email = userDetails.getUsername();
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

        // 첫번 째 권한 추출
        GrantedAuthority authority = authorities.stream().findFirst()
                .orElseThrow(() -> new ApiException(ExceptionEnum.ACCESS_DENIED_EXCEPTION));
        String role = authority.getAuthority();

        // 입력값 유효성 검사
        // 수혜자는 isMale, birth 값을 가질 수 없다
        if ("ROLE_CORPORATION".equals(role) || "ROLE_CORPORATIONYET".equals(role)) {
            if (modifyUserDto.getIsMale() != null) {
                throw new ApiException(ExceptionEnum.UNEXPECTED_ISMALE_EXCEPTION);
            }
            if (modifyUserDto.getBirth() != null) {
                throw new ApiException(ExceptionEnum.UNEXPECTED_BIRTH_EXCEPTION);
            }
        }
        // 사용자는 isMale, birth 값이 필수로 있어야 한다
        else if ("ROLE_CLIENT".equals(role)) {
            if (modifyUserDto.getIsMale() == null) {
                throw new ApiException(ExceptionEnum.MISSING_ISMALE_EXCEPTION);
            }
            if (modifyUserDto.getBirth() == null) {
                throw new ApiException(ExceptionEnum.MISSING_BIRTH_EXCEPTION);
            }
        }
        // 관리자는 isMale, birth, mobilePhone, landlinePhone 값을 가질 수 없다
        else if ("ROLE_ADMIN".equals(role)) {
            if (modifyUserDto.getIsMale() != null) {
                throw new ApiException(ExceptionEnum.UNEXPECTED_ISMALE_EXCEPTION);
            }
            if (modifyUserDto.getBirth() != null) {
                throw new ApiException(ExceptionEnum.UNEXPECTED_BIRTH_EXCEPTION);
            }
            if (modifyUserDto.getLandlinePhone() != null) {
                throw new ApiException(ExceptionEnum.UNEXPECTED_LANDLINE_PHONE_EXCEPTION);
            }
            if (modifyUserDto.getMobilePhone() != null) {
                throw new ApiException(ExceptionEnum.UNEXPECTED_MOBILE_PHONE_EXCEPTION);
            }
        }
        else {
            throw new ApiException(ExceptionEnum.ACCESS_DENIED_EXCEPTION);
        }

        UserDto savedUserDto = userService.modifyUserByEmail(email, modifyUserDto, profileImage);

        ResponseDto responseDto = ResponseDto.builder()
                .data(null)
                .build();

        if (savedUserDto.getRoles() == Roles.ROLE_CLIENT) {
            ClientViewDto clientViewDto = savedUserDto.toClientViewDto();
            responseDto.setData(clientViewDto);
        }
        else if (savedUserDto.getRoles() == Roles.ROLE_CORPORATION || savedUserDto.getRoles() == Roles.ROLE_CORPORATIONYET) {
            CorporationDetailViewDto corporationViewDto = savedUserDto.toCorporationViewDto();
            responseDto.setData(corporationViewDto);
        }
        else if (savedUserDto.getRoles() == Roles.ROLE_ADMIN) {
            AdminDetailViewDto adminDetailViewDto = savedUserDto.toAdminViewDto();
            responseDto.setData(adminDetailViewDto);
        }
        else {
            throw new ApiException(ExceptionEnum.ACCESS_DENIED_EXCEPTION);
        }

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    // JWT 토큰으로 회원 탈퇴
    @DeleteMapping
    public ResponseEntity<ResponseDto> deleteUserByToken(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();

        userService.withdrawUserByEmail(email);

        ResponseDto responseDto = ResponseDto.builder()
                .data(null)
                .build();

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    // JWT 토큰으로 비밀번호 확인
    @PostMapping("/password/verification")
    public ResponseEntity<ResponseDto> verifyPassword(@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody PasswordDto passwordDto) {
        String email = userDetails.getUsername();

        userService.verifyPassword(email, passwordDto);

        ResponseDto responseDto = ResponseDto.builder()
                .data(null)
                .build();

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    // 비밀번호 인증 코드 발송
    @PostMapping("/password/code")
    public ResponseEntity<ResponseDto> sendCodeForPasswordReset(@Valid @RequestBody EmailDto emailDto) throws MessagingException {
        userService.sendCodeForPasswordReset(emailDto);

        ResponseDto responseDto = ResponseDto.builder()
                .data(null)
                .build();

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    // 비밀번호 인증 코드 검증
    @PostMapping("/password/code/verification")
    public ResponseEntity<ResponseDto> verifyCodeForPasswordReset(@Valid @RequestBody EmailCodeDto emailCodeDto) {
        userService.verifyCodeForPasswordReset(emailCodeDto);

        ResponseDto responseDto = ResponseDto.builder()
                .data(null)
                .build();

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    // 비밀번호 재설정
    @PatchMapping("/password")
    public ResponseEntity<ResponseDto> resetPassword(@Valid @RequestBody PasswordResetDto passwordResetDto) {
        userService.resetPassword(passwordResetDto);

        ResponseDto responseDto = ResponseDto.builder()
                .data(null)
                .build();

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
}

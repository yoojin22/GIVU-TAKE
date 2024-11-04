package com.accepted.givutake.user.corporation.controller;

import com.accepted.givutake.global.model.ResponseDto;
import com.accepted.givutake.user.corporation.entity.BusinessRegistrationCertificates;
import com.accepted.givutake.user.corporation.service.CorporationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/corporations")
public class CorporationController {

    private final CorporationService corporationService;

    // 사업자 등록증 추가 및 수정
    @PostMapping("/certificates")
    public ResponseEntity<ResponseDto> addBusinessRegistrationCertificate(@AuthenticationPrincipal UserDetails userDetails,
                                                                          @RequestPart(value = "certificateImage") MultipartFile certificateImage) {
        String email = userDetails.getUsername();

        BusinessRegistrationCertificates savedBusinessRegistrationCertificates = corporationService.addBusinessRegistrationCertificate(email, certificateImage);

        Map<String, String> map = new HashMap<>();
        map.put("filePath", savedBusinessRegistrationCertificates.getFilePath());
        map.put("email", savedBusinessRegistrationCertificates.getUsers().getEmail());

        ResponseDto responseDto = ResponseDto.builder()
                .data(map)
                .build();

        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }
}

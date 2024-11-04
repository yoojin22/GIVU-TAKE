package com.accepted.givutake.user.corporation.service;

import com.accepted.givutake.global.enumType.ExceptionEnum;
import com.accepted.givutake.global.exception.ApiException;
import com.accepted.givutake.global.service.S3Service;
import com.accepted.givutake.user.common.entity.Users;
import com.accepted.givutake.user.common.model.UserDto;
import com.accepted.givutake.user.common.service.UserService;
import com.accepted.givutake.user.corporation.entity.BusinessRegistrationCertificates;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CorporationService {

    private final BusinessRegistrationCertificateService businessRegistrationCertificateService;
    private final UserService userService;
    private final S3Service s3Service;

    // 사업자 등록증 추가 및 수정
    public BusinessRegistrationCertificates addBusinessRegistrationCertificate(String email, MultipartFile certificateImage) {
        if (certificateImage.isEmpty()) {
            throw new ApiException(ExceptionEnum.MISSING_BUSINESS_REGISTRATION_CERTIFICATE_IMAGE_EXCEPTION);
        }

        // 1. DB에서 사용자 조회
        UserDto corporationyetDto = userService.getUserByEmail(email);
        Users corporationyet = corporationyetDto.toEntity();


        // 2. 사업자 등록증이 이미 있는지 확인
        BusinessRegistrationCertificates savedBusinessRegistrationCertificates = businessRegistrationCertificateService.findByUsers(corporationyet);

        // 3. 사업자 등록증이 없다면 s3 이용해서 사업자 등록증 추가
        if (savedBusinessRegistrationCertificates == null) {
            String publicCertificateImageUrl = null;

            if (!certificateImage.isEmpty()) {
                try {
                    publicCertificateImageUrl = s3Service.uploadCertificateImage(certificateImage);
                } catch (IOException e) {
                    throw new ApiException(ExceptionEnum.ILLEGAL_BUSINESS_REGISTRATION_CERTIFICATE_IMAGE_EXCEPTION);
                }
            }

            // 4. DB에 추가
            BusinessRegistrationCertificates businessRegistrationCertificates = BusinessRegistrationCertificates.builder()
                    .filePath(publicCertificateImageUrl)
                    .users(corporationyet)
                    .build();

            return businessRegistrationCertificateService.save(businessRegistrationCertificates);
        }
        // 5. 사업자 등록증이 이미 있다면 바꾸기
        else {
            // 기존의 사업자 등록증 사진 삭제
            String certificateImageUrl = savedBusinessRegistrationCertificates.getFilePath();
            if (certificateImageUrl != null) {
                String objectKey = s3Service.parseObjectKeyFromCloudfrontUrl(certificateImageUrl);
                s3Service.deleteCertificateImage(objectKey);
            }

            // 새로운 사업자 등록증 사진 업로드
            try {
                String modifiedCertificateImageUrl = s3Service.uploadCertificateImage(certificateImage);
                savedBusinessRegistrationCertificates.setFilePath(modifiedCertificateImageUrl);
            } catch (IOException e) {
                throw new ApiException(ExceptionEnum.ILLEGAL_BUSINESS_REGISTRATION_CERTIFICATE_IMAGE_EXCEPTION);
            }

            // DB에 재저장
            return businessRegistrationCertificateService.save(savedBusinessRegistrationCertificates);
        }
    }
}

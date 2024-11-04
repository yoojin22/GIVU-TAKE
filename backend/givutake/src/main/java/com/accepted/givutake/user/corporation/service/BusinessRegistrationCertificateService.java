package com.accepted.givutake.user.corporation.service;

import com.accepted.givutake.user.common.entity.Users;
import com.accepted.givutake.user.corporation.entity.BusinessRegistrationCertificates;
import com.accepted.givutake.user.corporation.repository.BusinessRegistrationCertificatesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BusinessRegistrationCertificateService {

    private final BusinessRegistrationCertificatesRepository businessRegistrationCertificatesRepository;

    public BusinessRegistrationCertificates findByUsers(Users corporationyet) {
        Optional<BusinessRegistrationCertificates> optionalBusinessRegistrationCertificates = businessRegistrationCertificatesRepository.findByUsers(corporationyet);
        return optionalBusinessRegistrationCertificates.orElse(null);
    }

    public BusinessRegistrationCertificates save(BusinessRegistrationCertificates businessRegistrationCertificates) {
        return businessRegistrationCertificatesRepository.save(businessRegistrationCertificates);
    }
}

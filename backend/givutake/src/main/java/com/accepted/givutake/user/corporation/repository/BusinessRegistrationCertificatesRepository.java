package com.accepted.givutake.user.corporation.repository;

import com.accepted.givutake.user.common.entity.Users;
import com.accepted.givutake.user.corporation.entity.BusinessRegistrationCertificates;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BusinessRegistrationCertificatesRepository extends JpaRepository<BusinessRegistrationCertificates, Integer> {

    Optional<BusinessRegistrationCertificates> findByUsers(Users corporationyet);
}

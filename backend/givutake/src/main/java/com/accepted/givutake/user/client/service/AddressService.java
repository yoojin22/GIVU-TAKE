package com.accepted.givutake.user.client.service;

import com.accepted.givutake.global.enumType.ExceptionEnum;
import com.accepted.givutake.global.exception.ApiException;
import com.accepted.givutake.user.client.entity.Addresses;
import com.accepted.givutake.user.client.repository.AddressRepository;
import com.accepted.givutake.user.common.entity.Users;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AddressService {

    private final AddressRepository addressRepository;

    // DB에 저장
    public Addresses saveAddresses(Addresses addresses) {
        return addressRepository.save(addresses);
    }

    // 이전의 대표주소를 false 처리
    public void updateRepresentativeAddressFalse(Users users) {
        Optional<Addresses> optionalRepresentativeAddress = addressRepository.findByUsersAndIsDeletedFalseAndIsRepresentativeTrue(users);

        if (optionalRepresentativeAddress.isPresent()) {
            Addresses representativeAddresses = optionalRepresentativeAddress.get();
            representativeAddresses.setRepresentative(false);
            addressRepository.save(representativeAddresses);
        }
    }

    // userIdx에 해당하는 유저의 모든 주소록 조회
    // (삭제 처리된 주소 제외하며, isRepresentative가 true인 정보 먼저 조회)
    public List<Addresses> getAddressesByUsers(Users users, Boolean isRepresentative) {

        Specification<Addresses> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 사용자 조건
            predicates.add(criteriaBuilder.equal(root.get("users"), users));

            // 삭제 조건
            predicates.add(criteriaBuilder.equal(root.get("isDeleted"), false));

            // 대표 주소 조건
            if (isRepresentative != null) {
                predicates.add(criteriaBuilder.equal(root.get("isRepresentative"), isRepresentative));
            }

            // 정렬 조건 (isRepresentative 내림차순)
            query.orderBy(criteriaBuilder.desc(root.get("isRepresentative")));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        return addressRepository.findAll(spec);
    }

    // addressIdx에 해당하는 주소 조회(삭제된 주소는 조회 불가)
    public Addresses getAddressByAddressIdx(int addressIdx) {
        Optional<Addresses> optionalExistingAddresses = addressRepository.findByAddressIdx(addressIdx);

        // DB에 존재하지 않을 경우
        if (optionalExistingAddresses.isEmpty()) {
            throw new ApiException(ExceptionEnum.NOT_FOUND_ADDRESSES_EXCEPTION);
        }

        // 삭제된 주소일 경우 조회 불가
        Addresses savedAddresses = optionalExistingAddresses.get();
        if (savedAddresses.isDeleted()) {
            throw new ApiException(ExceptionEnum.ADDRESSES_ALREADY_DELETED_EXCEPTION);
        }

        return savedAddresses;
    }

    // 유저의 대표 주소 조회
    public Addresses getRepresentativeAddressesByUsers(Users users) {
        Optional<Addresses> optionalAddresses = addressRepository.findByUsersAndIsDeletedFalseAndIsRepresentativeTrue(users);

        if (optionalAddresses.isPresent()) {
            return optionalAddresses.get();
        }
        else {
            throw new ApiException(ExceptionEnum.NOT_FOUND_REPRESENTATIVE_ADDRESS_EXCEPTION);
        }
    }

    // addressIdx에 해당하는 주소 삭제
    public Addresses deleteAddressByAddressIdx(Addresses addresses) {
        addresses.setDeleted(true);
        return addressRepository.save(addresses);
    }

    // 사용자의 총 주소 개수 조회
    public long countByUsers(Users users) {
        return addressRepository.countByUsers(users);
    }

    // 대표 주소 갯수 들고오기
    public int countByUsersAndIsRepresentativeTrue(Users users) {
        return addressRepository.countByUsersAndIsRepresentativeTrue(users);
    }
}

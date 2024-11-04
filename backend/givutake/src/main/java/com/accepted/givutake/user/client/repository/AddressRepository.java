package com.accepted.givutake.user.client.repository;

import com.accepted.givutake.user.client.entity.Addresses;
import com.accepted.givutake.user.common.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Addresses, Integer>, JpaSpecificationExecutor<Addresses> {
    List<Addresses> findByUsersAndIsDeletedFalseOrderByIsRepresentativeDesc(Users users);
    Optional<Addresses> findByUsersAndIsDeletedFalseAndIsRepresentativeTrue(Users users);
    Optional<Addresses> findByAddressIdx(int addressIdx);
    long countByUsers(Users users);
    int countByUsersAndIsRepresentativeTrue(Users users);

    @Query("SELECT a.isRepresentative FROM Addresses a WHERE a.addressIdx = :addressIdx")
    Boolean findIsRepresentativeByAddressIdx(@Param("addressIdx") int addressIdx);
}

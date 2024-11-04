package com.accepted.givutake.funding.repository;

import com.accepted.givutake.funding.entity.Fundings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface FundingRepository extends JpaRepository<Fundings, Integer>, JpaSpecificationExecutor<Fundings> {
    Optional<Fundings> findByFundingIdx(int fundingIdx);
    List<Fundings> findByFundingTypeAndStateAndIsDeletedFalse(char fundingType, byte state);
    List<Fundings> findByIsDeletedFalseAndState(byte state);
    List<Fundings> findTop10ByStateOrderByEndDate(byte state);

    @Modifying
    @Transactional
    @Query("UPDATE Fundings f SET f.isDeleted = true WHERE f.fundingIdx = :fundingIdx")
    int updateIsDeletedTrueByFundingIdx(@Param("fundingIdx") int fundingIdx);
}

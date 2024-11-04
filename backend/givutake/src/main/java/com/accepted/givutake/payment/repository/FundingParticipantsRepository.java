package com.accepted.givutake.payment.repository;

import com.accepted.givutake.payment.entity.FundingParticipants;
import com.accepted.givutake.user.common.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FundingParticipantsRepository extends JpaRepository<FundingParticipants, Long> {

    Page<FundingParticipants> findByUsers(Users user, Pageable pageable);
    List<FundingParticipants> findByUsersAndCreatedDateBetween(Users users, LocalDateTime startDate, LocalDateTime endDate);
    List<FundingParticipants> findByUsers(Users users);
    List<FundingParticipants> findByCreatedDateAfter(LocalDateTime startDate);
    List<FundingParticipants> findByCreatedDateBefore(LocalDateTime endDate);
    long countByUsers(Users users);

    @Query("SELECT SUM(fp.fundingFee) FROM FundingParticipants fp WHERE fp.users.userIdx = :userIdx")
    Long sumFundingFeeByUserIdx(@Param("userIdx") int userIdx);

    @Query("SELECT SUM(fp.fundingFee) FROM FundingParticipants fp")
    Long sumFundingFee();

    @Query("SELECT u.name AS name, SUM(p.fundingFee) AS totalPrice " +
            "FROM FundingParticipants p " +
            "JOIN p.users u " +
            "JOIN p.fundings f " +
            "WHERE f.fundingIdx = :fundingIdx " +
            "GROUP BY u.name")
    List<Object[]> findFundingParticipantsByFundingIdx(@Param("fundingIdx") int fundingIdx);
}

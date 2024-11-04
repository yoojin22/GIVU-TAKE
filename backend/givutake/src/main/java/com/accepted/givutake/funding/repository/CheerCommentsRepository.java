package com.accepted.givutake.funding.repository;

import com.accepted.givutake.funding.entity.CheerComments;
import com.accepted.givutake.funding.entity.Fundings;
import com.accepted.givutake.user.common.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface CheerCommentsRepository extends JpaRepository<CheerComments, Integer>  {

    Optional<CheerComments> findByCommentIdxAndFundings_FundingIdx(int commentIdx, int fundingIdx);
    List<CheerComments> findByFundingsAndIsDeletedFalseOrderByCommentIdxDesc(Fundings fundings);
    List<CheerComments> findByUsersAndIsDeletedFalseOrderByCommentIdxDesc(Users users);

    @Modifying
    @Transactional
    @Query("UPDATE CheerComments c SET c.isDeleted = true WHERE c.commentIdx = :commentIdx")
    int updateIsDeletedTrueByCommentIdx(@Param("commentIdx") int commentIdx);

}

package com.accepted.givutake.funding.repository;

import com.accepted.givutake.funding.entity.FundingReviews;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FundingReviewsRepository extends JpaRepository<FundingReviews, Integer> {
    Optional<FundingReviews> findByReviewIdx(int reviewIdx);
}

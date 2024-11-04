package com.accepted.givutake.gift.repository;

import com.accepted.givutake.gift.entity.GiftReviewLiked;
import com.accepted.givutake.gift.entity.GiftReviews;
import com.accepted.givutake.user.common.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GiftReviewLikedRepository extends JpaRepository<GiftReviewLiked, Long> {
    boolean existsByUserAndGiftReviews_ReviewIdx(Users user, int reviewIdx);
    Optional<GiftReviewLiked> findByUserAndGiftReviews(Users user, GiftReviews giftReviews);
}

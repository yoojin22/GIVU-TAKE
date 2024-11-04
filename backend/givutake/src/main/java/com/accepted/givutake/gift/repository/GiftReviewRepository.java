package com.accepted.givutake.gift.repository;

import com.accepted.givutake.gift.entity.GiftReviews;
import com.accepted.givutake.payment.entity.Orders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.NonNull;

public interface GiftReviewRepository extends JpaRepository<GiftReviews,Integer>, JpaSpecificationExecutor<GiftReviews> {
    @NonNull
    Page<GiftReviews> findAll(@NonNull Pageable pageable);

    boolean existsByOrdersAndIsDeleteFalse(Orders order);
}

package com.accepted.givutake.gift.repository;

import com.accepted.givutake.gift.entity.Gifts;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.NonNull;

import java.util.List;


public interface GiftRepository extends JpaRepository<Gifts,Integer>, JpaSpecificationExecutor<Gifts> {
    @NonNull
    Page<Gifts> findAll(@NonNull Pageable pageable); // 모두 검색

    List<Gifts> findTop10ByIsDeleteFalseOrderByAmountDesc();
}

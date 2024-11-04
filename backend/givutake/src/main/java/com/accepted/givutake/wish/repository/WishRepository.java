package com.accepted.givutake.wish.repository;

import com.accepted.givutake.gift.entity.Gifts;
import com.accepted.givutake.user.common.entity.Users;
import com.accepted.givutake.wish.entity.Wish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface WishRepository extends JpaRepository<Wish, Integer>, JpaSpecificationExecutor<Wish> {
    //Page<Wish> findAll(Pageable pageable);
    Optional<Wish> findByUsersAndGift(Users user, Gifts gift);
}

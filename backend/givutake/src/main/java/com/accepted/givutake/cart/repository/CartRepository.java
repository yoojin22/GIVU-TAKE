package com.accepted.givutake.cart.repository;

import com.accepted.givutake.cart.entity.Carts;
import com.accepted.givutake.user.common.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Carts,Integer> {
   Page<Carts> findByUsers(Users user, Pageable pageable);
}

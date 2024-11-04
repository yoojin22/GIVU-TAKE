package com.accepted.givutake.user.common.repository;

import com.accepted.givutake.user.common.entity.Users;
import com.accepted.givutake.user.common.enumType.Roles;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users, Integer> {
    Optional<Users> findByEmail(String email);

    Page<Users> findByRolesIn(List<Roles> roles, Pageable pageable);
    Page<Users> findByRoles(Roles roles, Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE Users u SET u.isWithdraw = :isWithdraw WHERE u.email = :email")
    void updateIsWithdrawByEmail(@Param("email") String email, @Param("isWithdraw") boolean isWithdraw);

    @Modifying
    @Transactional
    @Query("UPDATE Users u SET u.password = :password WHERE u.email = :email")
    void updatePasswordByEmail(@Param("email") String email, @Param("password") String password);

    @Query("SELECT u.profileImageUrl FROM Users u WHERE u.email = :email")
    Optional<String> findProfileImageUrlByEmail(@Param("email") String email);
}
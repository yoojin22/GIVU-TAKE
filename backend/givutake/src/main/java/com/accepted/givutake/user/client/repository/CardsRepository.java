package com.accepted.givutake.user.client.repository;

import com.accepted.givutake.user.client.entity.Cards;
import com.accepted.givutake.user.common.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CardsRepository extends JpaRepository<Cards, Integer> {

    boolean existsByCardNumberAndIsDeletedFalse(String cardNumber);
    Optional<Cards> findByUsersAndIsDeletedFalseAndIsRepresentativeTrue(Users users);
    Optional<Cards> findByCardIdx(int cardIdx);

    @Modifying
    @Query("UPDATE Cards c SET c.isDeleted = true WHERE c.cardIdx = :cardIdx")
    int updateIsDeletedTrueByCardIdx(int cardIdx);

    List<Cards> findByUsersAndIsDeletedFalse(Users users);
}

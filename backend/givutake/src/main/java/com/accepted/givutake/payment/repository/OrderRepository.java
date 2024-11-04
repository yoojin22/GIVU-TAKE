package com.accepted.givutake.payment.repository;

import com.accepted.givutake.gift.entity.Gifts;
import com.accepted.givutake.payment.entity.Orders;
import com.accepted.givutake.user.common.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Orders, Long> {
    Page<Orders> findByUsers(Users user, Pageable pageable);
    Optional<Orders> findTopByUsersOrderByCreatedDateDesc(Users user);
    List<Orders> findByUsers(Users users);
    List<Orders> findByUsersAndCreatedDateBefore(Users users, LocalDateTime endDateTime);
    List<Orders> findByUsersAndCreatedDateAfter(Users users, LocalDateTime startDateTime);
    List<Orders> findByUsersAndCreatedDateBetween(Users users, LocalDateTime startDateTime, LocalDateTime endDateTime);
    int countByGift(Gifts gift);
    @Query("SELECT SUM(o.price) FROM Orders o")
    Long getTotalOrderPrice();

    @Query("SELECT SUM(o.price) FROM Orders o WHERE o.users.userIdx = :userIdx")
    Long sumPriceByUserIdx(@Param("userIdx") int userIdx);


    @Query("SELECT FUNCTION('MONTH', o.createdDate) as month, SUM(o.amount) as total " +
            "FROM Orders o " +
            "JOIN o.gift g " +
            "JOIN g.corporations c " +
            "WHERE FUNCTION('YEAR', o.createdDate) = :year " +
            "AND (:giftIdx IS NULL OR g.giftIdx = :giftIdx) " +
            "AND c.userIdx = :corporationIdx " +
            "GROUP BY FUNCTION('MONTH', o.createdDate)")
    List<Object[]> findMonthlyOrderAmounts(@Param("corporationIdx") Integer corporationIdx, @Param("year") int year, @Param("giftIdx") Integer giftIdx);

    @Query("SELECT u.name AS name, SUM(o.price) AS totalPrice " +
            "FROM Orders o " +
            "JOIN o.users u " +
            "JOIN o.gift g " +
            "JOIN g.corporations c " +
            "WHERE (:giftIdx IS NULL OR g.giftIdx = :giftIdx) " +
            "AND c.userIdx = :corporationIdx " +
            "GROUP BY u.name")
    List<Object[]> findPurchasersByGiftIdx(@Param("giftIdx") Integer giftIdx, @Param("corporationIdx") Integer corporationIdx);
}

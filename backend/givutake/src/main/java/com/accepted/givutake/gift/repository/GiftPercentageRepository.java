package com.accepted.givutake.gift.repository;

import com.accepted.givutake.payment.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GiftPercentageRepository extends JpaRepository<Orders, Integer> {
    @Query(nativeQuery = true, value =
            """
            WITH order_stats AS (
                SELECT
                    o.gift_idx,
                    g.category_idx,
                    c.category_name,
                    u.is_male,
                    FLOOR(DATEDIFF(CURDATE(), u.birth) / 365) AS age,
                    o.amount,
                    SUM(o.amount) OVER() AS total_amount
                FROM orders o
                JOIN users u ON o.user_idx = u.user_idx
                JOIN gifts g ON o.gift_idx = g.gift_idx
                JOIN categories c ON g.category_idx = c.category_idx
            )
            SELECT
                'category' AS stat_type,
                category_name AS name,
                SUM(amount) AS count,
                SUM(amount) / MAX(total_amount) * 100 AS percentage
            FROM order_stats
            GROUP BY category_idx, category_name
    
            UNION ALL
    
            SELECT
                'gender' AS stat_type,
                IF(is_male, 'male', 'female') AS name,
                SUM(amount) AS count,
                SUM(amount) / MAX(total_amount) * 100 AS percentage
            FROM order_stats
            GROUP BY is_male
    
            UNION ALL
    
            SELECT
                'age' AS stat_type,
                CASE
                    WHEN age < 30 THEN '20s'
                    WHEN age < 40 THEN '30s'
                    WHEN age < 50 THEN '40s'
                    WHEN age < 60 THEN '50s'
                    ELSE '60+'
                END AS name,
                SUM(amount) AS count,
                SUM(amount) / MAX(total_amount) * 100 AS percentage
            FROM order_stats
            GROUP BY
                CASE
                    WHEN age < 30 THEN '20s'
                    WHEN age < 40 THEN '30s'
                    WHEN age < 50 THEN '40s'
                    WHEN age < 60 THEN '50s'
                    ELSE '60+'
                END
            """
    )
    List<Object[]> getOverallGiftStatistics();

    @Query(nativeQuery = true, value =
            """
            WITH category_gifts AS (
                SELECT g.category_idx
                FROM gifts g
                WHERE g.gift_idx = :giftIdx
            ),
            order_stats AS (
                SELECT
                    o.gift_idx,
                    g.gift_name,
                    u.is_male,
                    FLOOR(DATEDIFF(CURDATE(), u.birth) / 365) AS age,
                    o.amount,
                    SUM(o.amount) OVER (PARTITION BY g.category_idx) AS category_total_amount
                FROM orders o
                JOIN users u ON o.user_idx = u.user_idx
                JOIN gifts g ON o.gift_idx = g.gift_idx
                JOIN category_gifts cg ON g.category_idx = cg.category_idx
                WHERE g.corporation_idx = :userIdx
            ),
            gift_stats AS (
                SELECT
                    o.gift_idx,
                    g.gift_name,
                    u.is_male,
                    FLOOR(DATEDIFF(CURDATE(), u.birth) / 365) AS age,
                    o.amount,
                    SUM(o.amount) OVER () AS total_amount
                FROM orders o
                JOIN users u ON o.user_idx = u.user_idx
                JOIN gifts g ON o.gift_idx = g.gift_idx
                WHERE g.gift_idx = :giftIdx
            )
            SELECT
                'category' AS stat_type,
                gift_name AS name,
                SUM(amount) AS count,
                SUM(amount) / MAX(category_total_amount) * 100 AS percentage
            FROM order_stats
            GROUP BY gift_idx, gift_name
            
            UNION ALL
            
            SELECT
                'gender' AS stat_type,
                IF(is_male, 'male', 'female') AS name,
                SUM(amount) AS count,
                SUM(amount) / MAX(total_amount) * 100 AS percentage
            FROM gift_stats
            GROUP BY is_male
            
            UNION ALL
            
            SELECT
                'age' AS stat_type,
                CASE
                    WHEN age < 30 THEN '20s'
                    WHEN age < 40 THEN '30s'
                    WHEN age < 50 THEN '40s'
                    WHEN age < 60 THEN '50s'
                    ELSE '60+'
                END AS name,
                SUM(amount) AS count,
                SUM(amount) / MAX(total_amount) * 100 AS percentage
            FROM gift_stats
            GROUP BY
                CASE
                    WHEN age < 30 THEN '20s'
                    WHEN age < 40 THEN '30s'
                    WHEN age < 50 THEN '40s'
                    WHEN age < 60 THEN '50s'
                    ELSE '60+'
                END
            """
    )
    List<Object[]> getGiftStatisticsByGiftId(@Param("giftIdx") Integer giftIdx, @Param("userIdx") Long userIdx);
}

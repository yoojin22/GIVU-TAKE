package com.accepted.givutake.funding.repository;

import com.accepted.givutake.payment.entity.FundingParticipants;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface FundingStatisticsRepository extends JpaRepository<FundingParticipants, Integer> {
    @Query(nativeQuery = true, value = """
            SELECT
                IF(u.is_male = TRUE, 'male', 'female') AS gender,
                CASE
                    WHEN TIMESTAMPDIFF(YEAR, u.birth, CURDATE()) BETWEEN 20 AND 29 THEN '20s'
                    WHEN TIMESTAMPDIFF(YEAR, u.birth, CURDATE()) BETWEEN 30 AND 39 THEN '30s'
                    WHEN TIMESTAMPDIFF(YEAR, u.birth, CURDATE()) BETWEEN 40 AND 49 THEN '40s'
                    WHEN TIMESTAMPDIFF(YEAR, u.birth, CURDATE()) BETWEEN 50 AND 59 THEN '50s'
                    ELSE '60+'
                END AS age_group,
                IFNULL(SUM(fp.funding_fee), 0) AS total_funding
            FROM
                funding_participants fp
            JOIN
                users u ON fp.user_idx = u.user_idx
            WHERE
                fp.funding_idx = :fundingIdx
            GROUP BY
                gender, age_group
            ORDER BY
                gender, age_group
            """)
    List<Object[]> getFundingStatsByAgeAndGender(@Param("fundingIdx") Integer fundingIdx);
}

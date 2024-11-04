package com.accepted.givutake.region.repository;

import com.accepted.givutake.region.entity.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegionRepository extends JpaRepository<Region, Integer> {
    boolean existsByRegionIdx(int regionIdx);

    @Query("SELECT r FROM Region r WHERE r.sido LIKE CONCAT(:sido, '%') AND r.sigungu = :sigungu")
    Optional<Region> findRegionBySidoAndSigungu(@Param("sido") String sido, @Param("sigungu") String sigungu);

    @Query("SELECT r.regionIdx FROM Region r WHERE r.sido LIKE CONCAT(:sido, '%') AND r.sigungu = :sigungu")
    Integer findRegionIdxBySidoAndSigungu(@Param("sido") String sido, @Param("sigungu") String sigungu);

    @Query("SELECT DISTINCT r.sido FROM Region r")
    List<String> findDistinctSido();

    @Query("SELECT r.sigungu FROM Region r WHERE r.sido = :sido")
    List<String> findSigunguBySido(@Param("sido") String sido);
}
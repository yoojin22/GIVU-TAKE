package com.accepted.givutake.attraction.village.repository;

import com.accepted.givutake.attraction.village.entity.ExperienceVillage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ExperienceVillageRepository  extends JpaRepository<ExperienceVillage, Integer> {

    @Query("SELECT ev FROM ExperienceVillage ev WHERE ev.region.regionIdx = :regionIdx")
    Page<ExperienceVillage> findByRegionIdx(@Param("regionIdx") int regionIdx, Pageable pageable);

    @Query("SELECT ev FROM ExperienceVillage ev WHERE ev.region.regionIdx = :regionIdx AND ev.experienceVillageDivision LIKE %:division%")
    Page<ExperienceVillage> findByRegionIdxAndExperienceVillageDivisionLike(@Param("regionIdx") int regionIdx,
                                                          @Param("division") String division,
                                                          Pageable pageable);
}

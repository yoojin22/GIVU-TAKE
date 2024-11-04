package com.accepted.givutake.attraction.village.entity;

import com.accepted.givutake.region.entity.Region;
import jakarta.persistence.*;
import lombok.*;

@Table(name="Experience_village")
@Entity
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExperienceVillage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "experience_village_idx", nullable = false)
    private int experienceVillageIdx;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "region_idx", nullable = false)
    private Region region;

    @Column(name = "experience_village_name", nullable = false, length = 30)
    private String experienceVillageName;

    @Column(name = "experience_village_division", nullable = false, length = 100)
    private String experienceVillageDivision;

    @Column(name = "experience_village_program", nullable = false, length = 800)
    private String experienceVillageProgram;

    @Column(name = "experience_village_address", nullable = false, length = 100)
    private String experienceVillageAddress;

    @Column(name = "experience_village_phone", nullable = false, length = 13)
    private String experienceVillagePhone;

    @Column(name = "experience_village_homepage_url", nullable = false, length = 50)
    private String experienceVillageHomepageUrl;
}

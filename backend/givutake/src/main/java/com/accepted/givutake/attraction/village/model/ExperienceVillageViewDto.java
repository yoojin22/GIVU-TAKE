package com.accepted.givutake.attraction.village.model;

import com.accepted.givutake.attraction.village.entity.ExperienceVillage;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExperienceVillageViewDto {

    private int experienceVillageIdx;
    private String experienceVillageName;
    private String experienceVillageProgram;
    private String experienceVillageAddress;
    private String experienceVillagePhone;
    private String experienceVillageHomepageUrl;

    public static ExperienceVillageViewDto toDto(ExperienceVillage experienceVillage) {
        return ExperienceVillageViewDto.builder()
                .experienceVillageIdx(experienceVillage.getExperienceVillageIdx())
                .experienceVillageName(experienceVillage.getExperienceVillageName())
                .experienceVillageProgram(experienceVillage.getExperienceVillageProgram())
                .experienceVillageAddress(experienceVillage.getExperienceVillageAddress())
                .experienceVillagePhone(experienceVillage.getExperienceVillagePhone())
                .experienceVillageHomepageUrl(experienceVillage.getExperienceVillageHomepageUrl())
                .build();
    }
}

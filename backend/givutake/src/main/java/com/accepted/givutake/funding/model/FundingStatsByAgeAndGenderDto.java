package com.accepted.givutake.funding.model;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FundingStatsByAgeAndGenderDto {
    private Map<String, Long> maleData;
    private Map<String, Long> femaleData;

}

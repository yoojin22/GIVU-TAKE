package com.accepted.givutake.funding.model;

import com.accepted.givutake.funding.entity.CheerComments;
import com.accepted.givutake.funding.entity.Fundings;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class MyCheerCommentViewDto {

    private int fundingIdx;
    private String fundingTitle;
    private String fundingThumbnail;
    private int commentIdx;
    private String commentContent;
    private LocalDateTime createdDate;

    public static MyCheerCommentViewDto toDto(CheerComments cheerComments) {
        Fundings savedFundings = cheerComments.getFundings();
        return MyCheerCommentViewDto.builder()
                .fundingIdx(savedFundings.getFundingIdx())
                .fundingTitle(savedFundings.getFundingTitle())
                .fundingThumbnail(savedFundings.getFundingThumbnail())
                .commentIdx(cheerComments.getCommentIdx())
                .commentContent(cheerComments.getCommentContent())
                .createdDate(cheerComments.getCreatedDate())
                .build();
    }

}

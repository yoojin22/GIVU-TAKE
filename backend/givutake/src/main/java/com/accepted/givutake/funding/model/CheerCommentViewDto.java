package com.accepted.givutake.funding.model;

import com.accepted.givutake.funding.entity.CheerComments;
import com.accepted.givutake.user.common.entity.Users;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CheerCommentViewDto {

    private int commentIdx;
    private String email;
    private String name;
    private String commentContent;
    private LocalDateTime createdDate;

    public static CheerCommentViewDto toDto(CheerComments cheerComments) {
        Users users = cheerComments.getUsers();
        return CheerCommentViewDto.builder()
                .commentIdx(cheerComments.getCommentIdx())
                .email(users.getEmail())
                .name(users.getName())
                .commentContent(cheerComments.getCommentContent())
                .createdDate(cheerComments.getCreatedDate())
                .build();
    }
}

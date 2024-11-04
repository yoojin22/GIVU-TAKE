package com.accepted.givutake.funding.model;

import com.accepted.givutake.funding.entity.CheerComments;
import com.accepted.givutake.funding.entity.Fundings;
import com.accepted.givutake.user.common.entity.Users;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheerCommentAddDto {

    @Size(max = 400, message = "내용은 최대 400자까지 작성할 수 있습니다.")
    @NotBlank(message = "내용은 필수 입력 값 입니다.")
    private String commentContent;

    public CheerComments toEntity(Fundings fundings, Users users) {
        return CheerComments.builder()
                .fundings(fundings)
                .users(users)
                .commentContent(this.commentContent)
                .isDeleted(false)
                .build();
    }
}

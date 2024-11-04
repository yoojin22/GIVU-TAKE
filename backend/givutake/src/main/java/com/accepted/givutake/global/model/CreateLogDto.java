package com.accepted.givutake.global.model;

import com.accepted.givutake.global.enumType.ActEnum;
import com.accepted.givutake.global.enumType.ContentTypeEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@Builder
public class CreateLogDto {

    @NotBlank(message = "게시판 분류 값은 필수입니다.")
    private ContentTypeEnum contentType;

    @NotBlank(message = " 행동 값은 필수입니다.")
    private ActEnum act;

    @NotNull
    private int contentIdx;
}

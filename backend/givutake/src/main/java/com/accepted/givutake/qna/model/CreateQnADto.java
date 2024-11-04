package com.accepted.givutake.qna.model;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@Builder
public class CreateQnADto {

    @NotBlank(message = "Q&A 제목은 필수 입력 값입니다.")
    private String qnaTitle;

    @NotBlank(message = "Q&A 내용은 필수 입력 값입니다.")
    private String qnaContent;

}

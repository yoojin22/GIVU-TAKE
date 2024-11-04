package com.accepted.givutake.qna.model;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateAnswerDto {

    @NotBlank(message = "답변값은 필수 입력 값입니다.")
    private String answerContent;

}

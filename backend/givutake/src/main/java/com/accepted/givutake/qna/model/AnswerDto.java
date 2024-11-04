package com.accepted.givutake.qna.model;

import com.accepted.givutake.qna.entity.Answer;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@Builder
public class AnswerDto {
    private Integer answerIdx;
    private Integer userIdx;
    private String userName;
    private String userProfileImage;
    private Integer qnaIdx;
    private String answerContent;

    public static AnswerDto toDto(Answer answer) {
        return AnswerDto.builder()
                .answerIdx(answer.getAnswerIdx())
                .userIdx(answer.getAdmin().getUserIdx())
                .userName(answer.getAdmin().getName())
                .userProfileImage(answer.getAdmin().getProfileImageUrl())
                .qnaIdx(answer.getQna().getQnaIdx())
                .answerContent(answer.getAnswerContent())
                .build();
    }
}

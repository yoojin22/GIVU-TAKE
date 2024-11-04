package com.accepted.givutake.qna.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@Builder
public class QnADto {
    private Integer qnaIdx;
    private Integer userIdx;
    private String userName;
    private String userProfileImage;
    private String qnaTitle;
    private String qnaContent;
    private LocalDateTime createdDate;
    private AnswerDto answer;

}

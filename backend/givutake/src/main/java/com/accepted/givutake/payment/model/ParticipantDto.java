package com.accepted.givutake.payment.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@Builder
public class ParticipantDto {

    private Long participantIdx;
    private int fundingIdx;
    private String fundingTitle;
    private String fundingThumbnail;
    private char fundingType;
    private LocalDateTime participatedDate;
    private int price;

}

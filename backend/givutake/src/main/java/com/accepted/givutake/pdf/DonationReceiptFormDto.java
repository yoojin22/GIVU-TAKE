package com.accepted.givutake.pdf;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DonationReceiptFormDto {

    private String userName;
    private String userPhone;
    private String userAddress;
    private List<DonationParticipantsDto> donationParticipantsDtoList;
}

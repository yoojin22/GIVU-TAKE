package com.accepted.givutake.pdf;

import com.accepted.givutake.payment.entity.FundingParticipants;
import com.accepted.givutake.payment.entity.Orders;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DonationParticipantsDto implements Comparable<DonationParticipantsDto>{

    private String type;
    private String name;
    private LocalDate date;
    private int price;
    private String ref;
    
    public static DonationParticipantsDto ordersToDto(Orders orders, String ref) {
        return DonationParticipantsDto.builder()
                .type("지정기부금")
                .name(orders.getGift().getCorporations().getName())
                .date(orders.getCreatedDate().toLocalDate())
                .price(orders.getPrice())
                .ref(ref)
                .build();
    }
    
    public static DonationParticipantsDto fundingPariticipantsToDto(FundingParticipants fundingParticipants, String ref) {
        return DonationParticipantsDto.builder()
                .type("지정기부금")
                .name(fundingParticipants.getFundings().getCorporation().getName())
                .date(fundingParticipants.getCreatedDate().toLocalDate())
                .price(fundingParticipants.getFundingFee())
                .ref(ref)
                .build();
    }

    @Override
    public int compareTo(DonationParticipantsDto o) {
        // 날짜를 기준으로 최신 순으로 정렬
        return o.date.compareTo(this.date); // o.date가 최신이면 양수 반환
    }
}

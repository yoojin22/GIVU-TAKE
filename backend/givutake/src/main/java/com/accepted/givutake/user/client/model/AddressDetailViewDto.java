package com.accepted.givutake.user.client.model;

import com.accepted.givutake.user.client.entity.Addresses;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class AddressDetailViewDto extends AddressViewDto {

    private String zoneCode;
    private String buildingName;

    public static AddressDetailViewDto toDto(Addresses addresses) {
        return AddressDetailViewDto.builder()
                .addressIdx(addresses.getAddressIdx())
                .addressName(addresses.getAddressName())
                .roadAddress(addresses.getRoadAddress())
                .jibunAddress(addresses.getJibunAddress())
                .detailAddress(addresses.getDetailAddress())
                .isRepresentative(addresses.isRepresentative())
                .zoneCode(addresses.getZoneCode())
                .buildingName(addresses.getBuildingName())
                .build();
    }
}

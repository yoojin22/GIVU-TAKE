package com.accepted.givutake.user.client.model;

import com.accepted.givutake.user.client.entity.Addresses;
import com.accepted.givutake.user.common.entity.Users;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class AddressAddDto extends AddressSignUpDto {

    @NotNull(message = "대표 주소 여부는 필수 입력 값 입니다.")
    protected Boolean isRepresentative;

    public Addresses toEntity(Users users, int regionIdx) {
        return Addresses.builder()
                .users(users)
                .regionIdx(regionIdx)
                .addressName(this.addressName)
                .zoneCode(this.zoneCode)
                .roadAddress(this.roadAddress)
                .jibunAddress(this.jibunAddress)
                .detailAddress(this.detailAddress)
                .buildingName(this.buildingName)
                .isApartment(this.isApartment)
                .bname(this.bname)
                .bname1(this.bname1)
                .isRepresentative(this.isRepresentative)
//                .latitude(this.latitude)
//                .longitude(this.longitude)
                .build();
    }
}

package com.accepted.givutake.user.client.model;

import com.accepted.givutake.user.client.entity.Addresses;
import com.accepted.givutake.user.common.entity.Users;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class AddressSignUpDto {

    @NotBlank(message = "배송지명은 필수 입력 값 입니다.")
    @Size(max = 8, message = "배송지명은 최대 8자 이하여야 합니다.")
    protected String addressName;

    @NotBlank(message = "국가기초구역번호는 필수 입력 값 입니다.")
    @Size(max = 5, message = "국가기초구역번호는 최대 5자 이하여야 합니다.")
    protected String zoneCode;

    @NotBlank(message = "도로명 주소는 필수 입력 값 입니다.")
    @Size(max = 50, message = "도로명 주소는 최대 50자 이하여야 합니다.")
    protected String roadAddress;

    @NotBlank(message = "지번 주소는 필수 입력 값 입니다.")
    @Size(max = 50, message = "지번 주소 최대 50자 이하여야 합니다.")
    protected String jibunAddress;

    @NotBlank(message = "상세 주소는 필수 입력 값 입니다.")
    @Size(max = 50, message = "상세 주소는 최대 50자 이하여야 합니다.")
    protected String detailAddress;

    @NotBlank(message = "시/도명은 필수 입력 값 입니다.")
    @Size(max = 10, message = "시/도명은 최대 10자 이하여야 합니다.")
    protected String sido;

    @NotBlank(message = "시/군/구명은 필수 입력 값 입니다.")
    @Size(max = 10, message = "시/군/구명은 최대 10자 이하여야 합니다.")
    protected String sigungu;

    @NotNull(message = "건물명은 필수 입력 값 입니다.")
    @Size(max = 20, message = "건물명은 최대 20자 이하여야 합니다.")
    protected String buildingName;

    @NotNull(message = "공통주택 여부는 필수 입력 값 입니다.")
    protected Boolean isApartment;

    @NotNull(message = "법정동/법정리 이름은 필수 입력 값 입니다.")
    @Size(max = 10, message = "법정동/법정리 이름은 최대 10자 이하여야 합니다.")
    protected String bname;

    @NotNull(message = "법정리의 읍/면 이름은 필수 입력 값 입니다.")
    @Size(max = 10, message = "법정리의 읍/면 이름은 최대 10자 이하여야 합니다.")
    protected String bname1;

    // TODO: 추천 알고리즘에 사용
//    private BigDecimal latitude;
//    private BigDecimal longitude;

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
                .isRepresentative(true)
//                .latitude(this.latitude)
//                .longitude(this.longitude)
                .build();
    }
}

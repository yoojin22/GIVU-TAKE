package com.accepted.givutake.user.client.model;

import com.accepted.givutake.user.client.entity.Cards;
import com.accepted.givutake.user.common.entity.Users;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class AddCardDto {

    @NotBlank(message = "카드 회사는 필수 입력 값 입니다.")
    @Size(max = 30)
    private String cardCompany;

    @NotBlank(message = "카드 번호는 필수 입력 값 입니다.")
    @Pattern(regexp = "\\d{4}-\\d{4}-\\d{4}-\\d{4}\\b", message = "카드 번호 형식이 올바르지 않습니다. (형식: xxxx-xxxx-xxxx-xxxx)")
    private String cardNumber;

    @NotBlank(message = "카드 CVC는 필수 입력 값 입니다.")
    @Pattern(regexp = "^[0-9]{3}$", message = "카드 CVC 번호 형식이 올바르지 않습니다.")
    private String cardCVC;

    @NotNull(message = "카드 만료일은 필수 입력 값 입니다.")
    @Future(message = "만료된 카드는 등록할 수 없습니다.")
    private LocalDate cardExpiredDate;

    @NotBlank(message = "카드 비밀번호는 필수 입력 값 입니다.")
    @Pattern(regexp = "^[0-9]{4}$", message = "카드 비밀번호 형식이 올바르지 않습니다.")
    private String cardPassword;

    @NotNull(message = "대표 카드 여부는 필수 입력 값 입니다.")
    private Boolean isRepresentative;

    public Cards toEntity(Users users) {
        return Cards.builder()
                .users(users)
                .cardCompany(this.cardCompany)
                .cardNumber(this.cardNumber)
                .cardCVC(this.cardCVC)
                .cardExpiredDate(this.cardExpiredDate)
                .cardPassword(this.cardPassword)
                .isRepresentative(this.isRepresentative)
                .isDeleted(false)
                .build();
    }

}

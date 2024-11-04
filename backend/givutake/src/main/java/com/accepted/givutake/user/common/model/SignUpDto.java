package com.accepted.givutake.user.common.model;

import com.accepted.givutake.region.entity.Region;
import com.accepted.givutake.user.common.entity.Users;
import com.accepted.givutake.user.common.enumType.Roles;
import com.accepted.givutake.user.common.enumType.SocialType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
public class SignUpDto extends LoginDto {

    @NotBlank(message = "이름은 필수 입력 값 입니다.")
    private String name;
    
    private Boolean isMale; // 사용자만 입력

    private LocalDate birth; // 사용자만 입력

    private String sido; // 수혜자만 입력
    
    private String sigungu; // 수혜자만 입력

    @Pattern(regexp = "^\\d{3}-\\d{3,4}-\\d{4}$", message = "휴대폰 번호 형식이 올바르지 않습니다.")
    private String mobilePhone;

    @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$", message = "일반 전화 번호 형식이 올바르지 않습니다.")
    private String landlinePhone;

    @NotNull(message = "권한은 필수 입력 값 입니다.")
    private Roles roles;

    @NotNull(message = "소셜 로그인 여부는 필수 입력 값 입니다.")
    private boolean isSocial;

    private SocialType socialType;

    private String socialSerialNum;

    public Users toEntity(Region region, String profileImageUrl) {
        return Users.builder()
                .name(this.name)
                .email(this.email)
                .password(this.password)
                .mobilePhone(this.mobilePhone)
                .landlinePhone(this.landlinePhone)
                .isMale(this.isMale)
                .birth(this.birth)
                .region(region)
                .profileImageUrl(profileImageUrl)
                .roles(this.roles)
                .isSocial(this.isSocial)
                .socialType(this.socialType)
                .socialSerialNum(this.socialSerialNum)
                .isWithdraw(false)
                .build();
    }

}

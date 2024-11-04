package com.accepted.givutake.user.common.entity;

import com.accepted.givutake.global.entity.BaseTimeEntity;
import com.accepted.givutake.region.entity.Region;
import com.accepted.givutake.user.common.enumType.Roles;
import com.accepted.givutake.user.common.enumType.SocialType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Table(name="Users")
@Entity
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Users extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_idx", nullable = false)
    private int userIdx;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "mobile_phone")
    private String mobilePhone;

    @Column(name = "landline_phone")
    private String landlinePhone;

    // 사용자만 해당
    @Column(name = "is_male")
    private Boolean isMale;

    // 사용자만 해당
    @Column(name = "birth")
    private LocalDate birth;

    // 수혜자만 해당
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "region_idx")
    private Region region;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Column(name = "roles", nullable = false)
    @Enumerated(EnumType.STRING)
    private Roles roles;

    @Column(name = "is_social", nullable = false)
    private boolean isSocial;

    @Column(name = "social_type")
    @Enumerated(EnumType.STRING)
    private SocialType socialType;

    @Column(name = "social_serial_num")
    private String socialSerialNum;

    @Column(name = "is_withdraw", nullable = false)
    private boolean isWithdraw;
}
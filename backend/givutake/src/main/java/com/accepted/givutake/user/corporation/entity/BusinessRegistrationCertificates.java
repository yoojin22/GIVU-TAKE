package com.accepted.givutake.user.corporation.entity;

import com.accepted.givutake.global.entity.BaseTimeEntity;
import com.accepted.givutake.user.common.entity.Users;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@Table(name="Business_registration_certificates")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessRegistrationCertificates extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "certificate_idx", nullable = false)
    private int certificateIdx;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_idx", nullable = false)
    private Users users;

    @Column(name = "file_path", nullable = false, length = 2048)
    private String filePath;
}

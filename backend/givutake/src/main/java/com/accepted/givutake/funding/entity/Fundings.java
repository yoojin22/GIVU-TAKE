package com.accepted.givutake.funding.entity;

import com.accepted.givutake.global.entity.BaseTimeEntity;
import com.accepted.givutake.payment.entity.FundingParticipants;
import com.accepted.givutake.user.common.entity.Users;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Table(name="Fundings")
@Entity
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Fundings extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "funding_idx", nullable = false)
    private int fundingIdx;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "corporation_idx", nullable = false)
    private Users corporation;

    @Column(name = "funding_title", length = 30, nullable = false)
    private String fundingTitle;

    @Column(name = "funding_content", length = 6000)
    private String fundingContent;

    @Column(name = "funding_content_image", length = 2048)
    private String fundingContentImage;

    @OneToOne(mappedBy = "fundings", orphanRemoval = true, fetch = FetchType.EAGER)
    private FundingReviews fundingReviews;

    @Builder.Default
    @OneToMany(mappedBy = "fundings", orphanRemoval = true, fetch = FetchType.LAZY)
    private List<FundingParticipants> fundingParticipantsList = new ArrayList<>();

    @Column(name = "goal_money", nullable = false)
    private int goalMoney;

    @Column(name = "total_money", nullable = false)
    private int totalMoney;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "funding_thumbnail", length = 2048)
    private String fundingThumbnail;

    @Column(name = "funding_type", nullable = false, length = 1)
    private char fundingType;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    @Column(name = "state", nullable = false)
    private byte state;
}

package com.accepted.givutake.funding.entity;

import com.accepted.givutake.global.entity.BaseTimeEntity;
import com.accepted.givutake.user.common.entity.Users;
import jakarta.persistence.*;
import lombok.*;

@Table(name="Cheer_comments")
@Entity
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheerComments extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_idx", nullable = false)
    private int commentIdx;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "funding_idx", nullable = false)
    private Fundings fundings;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_idx", nullable = false)
    private Users users;

    @Column(name = "comment_content", nullable = false, length = 400)
    private String commentContent;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;
}

package com.accepted.givutake.qna.entity;

import com.accepted.givutake.global.entity.BaseTimeEntity;
import com.accepted.givutake.user.common.entity.Users;
import jakarta.persistence.*;
import lombok.*;

@Table(name = "qna")
@Entity
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QnA extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 답례품 ID
    @Column(name = "qna_idx")
    private int qnaIdx;

    @ManyToOne(targetEntity = Users.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_idx", referencedColumnName = "user_idx")
    private Users users;

    @Column(name = "qna_title", nullable = false, length = 50)
    private String qnaTitle;

    @Column(name = "qna_content", nullable = false, length = 1000)
    private String qnaContent;

}

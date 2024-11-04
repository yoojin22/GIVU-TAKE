package com.accepted.givutake.qna.entity;

import com.accepted.givutake.global.entity.BaseTimeEntity;
import com.accepted.givutake.user.common.entity.Users;
import jakarta.persistence.*;
import lombok.*;

@Table(name = "answer")
@Entity
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Answer extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 답례품 ID
    @Column(name = "answer_idx")
    private int answerIdx;

    @ManyToOne(targetEntity = Users.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_idx", referencedColumnName = "user_idx")
    private Users admin;

    @OneToOne(targetEntity = QnA.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "qna_idx", referencedColumnName = "qna_idx")
    private QnA qna;

    @Column(name = "answer_content", nullable = false, length = 1000)
    private String answerContent;
}

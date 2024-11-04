package com.accepted.givutake.global.entity;

import com.accepted.givutake.global.enumType.ActEnum;
import com.accepted.givutake.global.enumType.ContentTypeEnum;
import com.accepted.givutake.user.common.entity.Users;
import jakarta.persistence.*;
import lombok.*;

@Table(name = "user_view_logs")
@Entity
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserViewLogs extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 로그 ID
    @Column(name = "log_idx")
    private long logIdx;

    @ManyToOne(targetEntity = Users.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_idx", referencedColumnName = "user_idx")// 회원 ID 외래키 설정
    private Users users;

    @Enumerated(EnumType.STRING) // 게시판 분류
    private ContentTypeEnum contentType;

    @Enumerated(EnumType.STRING) // 행동 분류
    private ActEnum actEnum;

    @Column(name = "content_idx", nullable = false) // 게시판 ID
    private int contentIdx;
    
}

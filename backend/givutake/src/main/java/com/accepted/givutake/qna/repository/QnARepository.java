package com.accepted.givutake.qna.repository;

import com.accepted.givutake.qna.entity.QnA;
import com.accepted.givutake.user.common.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

public interface QnARepository extends JpaRepository<QnA, Integer> {
    @NonNull
    Page<QnA> findAll(@NonNull Pageable pageable);

    Page<QnA> findByUsers(Users user, Pageable pageable);
}

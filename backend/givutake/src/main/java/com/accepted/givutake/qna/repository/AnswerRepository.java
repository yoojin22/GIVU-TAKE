package com.accepted.givutake.qna.repository;

import com.accepted.givutake.qna.entity.Answer;
import com.accepted.givutake.qna.entity.QnA;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AnswerRepository extends JpaRepository<Answer, Integer> {
    Optional<Answer> findByQna(QnA qna);
}

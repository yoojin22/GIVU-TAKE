package com.accepted.givutake.qna.service;

import com.accepted.givutake.global.enumType.ExceptionEnum;
import com.accepted.givutake.global.exception.ApiException;
import com.accepted.givutake.qna.entity.QnA;
import com.accepted.givutake.qna.model.AnswerDto;
import com.accepted.givutake.qna.model.CreateQnADto;
import com.accepted.givutake.qna.model.QnADto;
import com.accepted.givutake.qna.repository.AnswerRepository;
import com.accepted.givutake.qna.repository.QnARepository;
import com.accepted.givutake.user.common.entity.Users;
import com.accepted.givutake.user.common.repository.UsersRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class QnAService {

    private final QnARepository qnARepository;
    private final UsersRepository usersRepository;
    private final AnswerRepository answerRepository;

    public void createQnA(String email, CreateQnADto request){
        Users user = usersRepository.findByEmail(email).orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_USER_WITH_EMAIL_EXCEPTION));
        QnA newQnA = QnA.builder()
                .users(user)
                .qnaTitle(request.getQnaTitle())
                .qnaContent(request.getQnaContent())
                .build();
        qnARepository.save(newQnA);
    }

    public List<QnADto> getQnAList(String email,int pageNo, int pageSize){
        Pageable pageable = PageRequest.of(pageNo-1, pageSize, Sort.by(Sort.Direction.DESC, "createdDate"));

        Users user = usersRepository.findByEmail(email).orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_USER_WITH_EMAIL_EXCEPTION));

        Page<QnA> qnaList = qnARepository.findByUsers(user, pageable);

        return qnaList.map(qna -> QnADto.builder()
                .qnaIdx(qna.getQnaIdx())
                .userIdx(user.getUserIdx())
                .userName(user.getName())
                .userProfileImage(user.getProfileImageUrl())
                .qnaTitle(qna.getQnaTitle())
                .qnaContent(qna.getQnaContent())
                .createdDate(qna.getCreatedDate())
                .answer(answerRepository.findByQna(qna)
                        .map(AnswerDto::toDto)
                        .orElse(null))
                .build()
        ).toList();
    }

    public List<QnADto> getQnAadminList(int pageNo, int pageSize){
        Pageable pageable = PageRequest.of(pageNo-1, pageSize, Sort.by(Sort.Direction.DESC, "createdDate"));

        Page<QnA> qnaList = qnARepository.findAll(pageable);

        return qnaList.map(qna -> QnADto.builder()
                .qnaIdx(qna.getQnaIdx())
                .userIdx(qna.getUsers().getUserIdx())
                .userName(qna.getUsers().getName())
                .userProfileImage(qna.getUsers().getProfileImageUrl())
                .qnaTitle(qna.getQnaTitle())
                .qnaContent(qna.getQnaContent())
                .createdDate(qna.getCreatedDate())
                .answer(answerRepository.findByQna(qna)
                        .map(AnswerDto::toDto)
                        .orElse(null))
                .build()
        ).toList();
    }

    public QnADto getQnA(String authority ,String email, int qnaIdx){
        QnA qna = qnARepository.findById(qnaIdx).orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_QNA_EXCEPTION));
        if(!(qna.getUsers().getEmail().equals(email)||authority.equals("ROLE_ADMIN"))){ // 관리자 접근 구현
            throw new ApiException(ExceptionEnum.ACCESS_DENIED_EXCEPTION);
        }
        return QnADto.builder()
                .qnaIdx(qna.getQnaIdx())
                .userIdx(qna.getUsers().getUserIdx())
                .userName(qna.getUsers().getName())
                .userProfileImage(qna.getUsers().getProfileImageUrl())
                .qnaTitle(qna.getQnaTitle())
                .qnaContent(qna.getQnaContent())
                .build();
    }

    public void deleteQnA(String email, int qnaIdx){
        QnA qna = qnARepository.findById(qnaIdx).orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_QNA_EXCEPTION));
        if(!qna.getUsers().getEmail().equals(email)){
            throw new ApiException(ExceptionEnum.ACCESS_DENIED_EXCEPTION);
        }
        qnARepository.delete(qna);
    }
}

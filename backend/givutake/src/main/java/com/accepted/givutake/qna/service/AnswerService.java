package com.accepted.givutake.qna.service;

import com.accepted.givutake.global.enumType.ExceptionEnum;
import com.accepted.givutake.global.exception.ApiException;
import com.accepted.givutake.qna.entity.Answer;
import com.accepted.givutake.qna.entity.QnA;
import com.accepted.givutake.qna.model.AnswerDto;
import com.accepted.givutake.qna.model.CreateAnswerDto;
import com.accepted.givutake.qna.repository.AnswerRepository;
import com.accepted.givutake.qna.repository.QnARepository;
import com.accepted.givutake.user.admin.service.AdminService;
import com.accepted.givutake.user.common.entity.Users;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final AdminService adminService;
    private final QnARepository qnARepository;

    public void createAnswer(String authority , String email, int qnaIdx, CreateAnswerDto request){
        Users admin = adminService.getUserByEmail(email);
        QnA qna = qnARepository.findById(qnaIdx).orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_QNA_EXCEPTION));
        if(!authority.equals("ROLE_ADMIN")){
            throw new ApiException(ExceptionEnum.ACCESS_DENIED_EXCEPTION);
        }
        Answer newAnswer = Answer.builder()
                .admin(admin)
                .qna(qna)
                .answerContent(request.getAnswerContent())
                .build();
        answerRepository.save(newAnswer);
    }

    public AnswerDto getAnswer(String authority, String email, int qnaIdx){
        QnA qna = qnARepository.findById(qnaIdx).orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_QNA_EXCEPTION));
        Answer answer = answerRepository.findByQna(qna).orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_QNA_ANSWER_EXCEPTION));
        if(!(authority.equals("ROLE_ADMIN")||qna.getUsers().getEmail().equals(email))){
            throw new ApiException(ExceptionEnum.ACCESS_DENIED_EXCEPTION);
        }
        return AnswerDto.builder()
                .answerIdx(answer.getAnswerIdx())
                .userIdx(answer.getAdmin().getUserIdx())
                .userName(answer.getAdmin().getName())
                .userProfileImage(answer.getAdmin().getProfileImageUrl())
                .qnaIdx(qnaIdx)
                .answerContent(answer.getAnswerContent())
                .build();
    }

    public void deleteAnswer(String authority, int answerIdx){
        Answer answer = answerRepository.findById(answerIdx).orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_QNA_EXCEPTION));
        if(!authority.equals("ROLE_ADMIN")){
            throw new ApiException(ExceptionEnum.ACCESS_DENIED_EXCEPTION);
        }
        answerRepository.delete(answer);
    }

}

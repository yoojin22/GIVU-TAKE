package com.accepted.givutake.qna.controller;

import com.accepted.givutake.global.enumType.ExceptionEnum;
import com.accepted.givutake.global.exception.ApiException;
import com.accepted.givutake.global.model.ResponseDto;
import com.accepted.givutake.qna.model.AnswerDto;
import com.accepted.givutake.qna.model.CreateAnswerDto;
import com.accepted.givutake.qna.service.AnswerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/answer")
@CrossOrigin
public class AnswerController {

    private final AnswerService answerService;

    @GetMapping
    public ResponseEntity<ResponseDto> getAnswer(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("qnaIdx") int qnaIdx){
        GrantedAuthority firstAuthority = userDetails.getAuthorities().stream().findFirst().orElseThrow(() -> new ApiException(ExceptionEnum.ACCESS_DENIED_EXCEPTION));
        AnswerDto answer = answerService.getAnswer(firstAuthority.getAuthority(), userDetails.getUsername(), qnaIdx);
        ResponseDto responseDto = ResponseDto.builder()
                .data(answer)
                .build();
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ResponseDto> createAnswer(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("qnaIdx") int qnaIdx,
            @Valid @RequestBody CreateAnswerDto request){
        GrantedAuthority firstAuthority = userDetails.getAuthorities().stream().findFirst().orElseThrow(() -> new ApiException(ExceptionEnum.ACCESS_DENIED_EXCEPTION));
        answerService.createAnswer(firstAuthority.getAuthority(), userDetails.getUsername(), qnaIdx, request);
        ResponseDto responseDto = ResponseDto.builder()
                .data(null)
                .build();
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @DeleteMapping("/{answerIdx}")
    public ResponseEntity<ResponseDto> deleteAnswer(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable int answerIdx){
        GrantedAuthority firstAuthority = userDetails.getAuthorities().stream().findFirst().orElseThrow(() -> new ApiException(ExceptionEnum.ACCESS_DENIED_EXCEPTION));
        answerService.deleteAnswer(firstAuthority.getAuthority(), answerIdx);
        ResponseDto responseDto = ResponseDto.builder()
                .data(null)
                .build();
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
}

package com.accepted.givutake.qna.controller;

import com.accepted.givutake.global.enumType.ExceptionEnum;
import com.accepted.givutake.global.exception.ApiException;
import com.accepted.givutake.global.model.ResponseDto;
import com.accepted.givutake.qna.model.CreateQnADto;
import com.accepted.givutake.qna.model.QnADto;
import com.accepted.givutake.qna.service.QnAService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/qna")
@CrossOrigin
public class QnAController {

    private final QnAService qnAService;

    @GetMapping
    public ResponseEntity<ResponseDto> getQnaList(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(value = "pageNo", defaultValue = "1")int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10")int pageSize){
        List<QnADto> qnaList;
        GrantedAuthority firstAuthority = userDetails.getAuthorities().stream().findFirst().orElseThrow(() -> new ApiException(ExceptionEnum.ACCESS_DENIED_EXCEPTION));
        if(firstAuthority.getAuthority().equals("ROLE_ADMIN")){
            qnaList = qnAService.getQnAadminList(pageNo, pageSize);
        }else {
            qnaList = qnAService.getQnAList(userDetails.getUsername(), pageNo, pageSize);
        }
        ResponseDto responseDto = ResponseDto.builder()
                .data(qnaList)
                .build();
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping("/{qnaIdx}")
    public ResponseEntity<ResponseDto> getQna(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable int qnaIdx) {
        GrantedAuthority firstAuthority = userDetails.getAuthorities().stream().findFirst().orElseThrow(() -> new ApiException(ExceptionEnum.ACCESS_DENIED_EXCEPTION));
        QnADto qna = qnAService.getQnA(firstAuthority.getAuthority(), userDetails.getUsername(), qnaIdx);
        ResponseDto responseDto = ResponseDto.builder()
                .data(qna)
                .build();
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ResponseDto> createQna(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateQnADto request){
        qnAService.createQnA(userDetails.getUsername(), request);
        ResponseDto responseDto = ResponseDto.builder()
                .data(null)
                .build();
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @DeleteMapping("/{qnaIdx}")
    public ResponseEntity<ResponseDto> deleteQna(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable int qnaIdx){
        qnAService.deleteQnA(userDetails.getUsername(), qnaIdx);
        ResponseDto responseDto = ResponseDto.builder()
                .data(null)
                .build();
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
}

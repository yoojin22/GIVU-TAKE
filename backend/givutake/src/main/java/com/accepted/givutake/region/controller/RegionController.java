package com.accepted.givutake.region.controller;

import com.accepted.givutake.global.model.ResponseDto;
import com.accepted.givutake.region.service.RegionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/regions")
public class RegionController {

    private final RegionService regionService;

    // 모든 시/도명 리스트를 반환
    @GetMapping("/sido")
    public ResponseEntity<ResponseDto> getSidoList() {

        List<String> sidoList = regionService.getSidoList();

        ResponseDto responseDto = ResponseDto.builder()
                .data(sidoList)
                .build();

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    // 시/도명에 해당하는 모든 시군구 리스트 반환
    @GetMapping("/sigungu")
    public ResponseEntity<ResponseDto> getSigunguListBySido(@RequestParam String sido) {

        List<String> sigunguList = regionService.getSigunguListBySido(sido);

        ResponseDto responseDto = ResponseDto.builder()
                .data(sigunguList)
                .build();

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
}

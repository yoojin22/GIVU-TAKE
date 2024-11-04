package com.accepted.givutake.global.controller;

import com.accepted.givutake.global.model.ResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/key")
public class ApiController {

    private final PasswordEncoder passwordEncoder;

    @Value("${api.weather}")
    String weatherKey;

    @Value("${api.market}")
    String marketKey;

    @Value("${api.attraction}")
    String attractionKey;

    @Value("${api.map}")
    String mapKey;

    @Value("${api.street}")
    String streetKey;

    @Value("${api.ocr}")
    String ocrKey;

    @GetMapping("/weather")
    public ResponseEntity<ResponseDto> weather() {
        ResponseDto responseDto = ResponseDto.builder()
                .data(passwordEncoder.encode(weatherKey))
                .build();
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping("/market")
    public ResponseEntity<ResponseDto> market() {
        ResponseDto responseDto = ResponseDto.builder()
                .data(passwordEncoder.encode(marketKey))
                .build();
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping("/attraction")
    public ResponseEntity<ResponseDto> attraction() {
        ResponseDto responseDto = ResponseDto.builder()
                .data(passwordEncoder.encode(attractionKey))
                .build();
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping("/map")
    public ResponseEntity<ResponseDto> map() {
        ResponseDto responseDto = ResponseDto.builder()
                .data(passwordEncoder.encode(mapKey))
                .build();
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping("/street")
    public ResponseEntity<ResponseDto> street() {
        ResponseDto responseDto = ResponseDto.builder()
                .data(passwordEncoder.encode(streetKey))
                .build();
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping("/ocr")
    public ResponseEntity<ResponseDto> ocr() {
        ResponseDto responseDto = ResponseDto.builder()
                .data(passwordEncoder.encode(ocrKey))
                .build();
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

}

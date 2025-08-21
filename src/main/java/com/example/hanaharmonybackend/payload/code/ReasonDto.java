package com.example.hanaharmonybackend.payload.code;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@Builder
public class ReasonDto {

    private HttpStatus httpStatus;
    private final String code;
    private final String message;
}
package com.example.hanaharmonybackend.payload.exception;

import com.example.hanaharmonybackend.payload.code.BaseStatus;
import com.example.hanaharmonybackend.payload.code.ReasonDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomException extends RuntimeException {

    private BaseStatus status;

    public ReasonDto getErrorReason() {
        return this.status.getReason();
    }

}
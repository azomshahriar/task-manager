package com.example.taskmanager.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BadRequestException extends RuntimeException {

    private String code;

    public BadRequestException(String code, String msg) {
        super(msg);
        this.code = code;
    }
}

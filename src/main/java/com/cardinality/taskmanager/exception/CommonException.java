package com.cardinality.taskmanager.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommonException  extends RuntimeException {

    private String code;

    public CommonException(String code, String msg) {
        super(msg);
        this.code = code;
    }
}
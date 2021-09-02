package com.cardinality.taskmanager.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ElementNotFoundException extends RuntimeException {

    private String code;

    public ElementNotFoundException(String code, String msg) {
        super(msg);
        this.code = code;
    }
}
package com.spec2test.application.common.exception;

import lombok.Getter;

import java.util.Map;

@Getter
public class InvalidStateException extends RuntimeException {

    private final Map<String, String> params;

    public InvalidStateException(ErrorCode errorCode) {
        this(errorCode, Map.of());
    }

    public InvalidStateException(ErrorCode errorCode, Map<String, String> params) {
        super(errorCode.name());
        this.params = params;
    }
}

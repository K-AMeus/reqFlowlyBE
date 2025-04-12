package com.spec2test.application.common.exception;

import lombok.Value;

import java.time.Instant;
import java.util.Map;

@Value
public class ApiErrorResponse {

    String message;
    Map<String, String> params;
    Instant timestamp = Instant.now();
}

package com.springbootjwtauth.exception;

import com.springbootjwtauth.dto.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException ex) {
        ErrorCode ec = ex.getErrorCode();
        ErrorResponse body = new ErrorResponse(ec.getCode(), ec.getMessage());

        HttpStatus status;
        switch (ec) {
            case INVALID_TOKEN:
                status = HttpStatus.UNAUTHORIZED;   // 401
                break;
            case ACCESS_DENIED:
                status = HttpStatus.FORBIDDEN;      // 403
                break;
            default:
                status = HttpStatus.BAD_REQUEST;    // 400
        }

        return ResponseEntity.status(status).body(body);
    }

}

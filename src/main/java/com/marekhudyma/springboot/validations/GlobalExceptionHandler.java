package com.marekhudyma.springboot.validations;


import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Problem> errorHandler(Exception e) {
    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new Problem("/validations/errors",
            "INTERNAL_SERVER_ERROR",
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            e.getMessage(),
            "/validations/errors"));
  }


  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Problem> errorHandler(MethodArgumentNotValidException e) {
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(new Problem("/validations/violations",
            e.getMessage(),
            HttpStatus.BAD_REQUEST.value(),
            e.getMessage(),
            "/validations/violations"));
  }

  @ExceptionHandler(ValidationException.class)
  public ResponseEntity<Problem> errorHandler(ValidationException e) {
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(new Problem("/validations/violations",
            e.getMessage(),
            HttpStatus.BAD_REQUEST.value(),
            e.getMessage(),
            "/validations/violations"));
  }

  record Problem(String type,
                 String title,
                 int status,
                 String detail,
                 String instance) {
  }


}

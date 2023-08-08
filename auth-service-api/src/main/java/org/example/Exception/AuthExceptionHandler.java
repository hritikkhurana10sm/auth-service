package org.example.Exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZonedDateTime;

@ControllerAdvice
public class AuthExceptionHandler {

    @ExceptionHandler({AuthException.class})
    public ResponseEntity<Object> handleAuthException( AuthException ex){
        AuthExceptionModel apiExceptionModel=new AuthExceptionModel(
                ZonedDateTime.now(),
                ex.getHttpStatus(),
                ex.getMessages()
        );
        return new ResponseEntity<>(apiExceptionModel, ex.getHttpStatus());
    }
}

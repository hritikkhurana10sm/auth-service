package org.example.Exception;

import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

public class AuthException extends RuntimeException{
    private List<String> messages=new ArrayList<>();
    private final HttpStatus httpStatus;

    public AuthException(String message, HttpStatus httpStatus) {
        this.messages.add(message);
        this.httpStatus = httpStatus;
    }

    public AuthException(List<String> messages, HttpStatus httpStatus) {
        this.messages.addAll(messages);
        this.httpStatus = httpStatus;
    }

    public List<String> getMessages() {
        return messages;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}

package org.example.generics;

import org.springframework.lang.Nullable;

import java.util.List;

public class Response<T> {
    private final T body;
    private final List<String> messages;

    public Response(T body, @Nullable List<String> messages) {
        this.body = body;
        this.messages = messages;
    }

    public T getBody() {
        return body;
    }

    public List<String> getMessages() {
        return messages;
    }
}

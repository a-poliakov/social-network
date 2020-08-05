package ru.apolyakov.social_network.utils.rest;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static ru.apolyakov.social_network.utils.rest.ResponseStatus.*;

@Data
@NoArgsConstructor
public class Response<T> {
    private T data;

    private String result;
    private String msg;

    public Response(T data) {
        this.data = data;
    }

    public Response(Boolean success) {
        this.setResult(success ? ResponseStatus.SUCCESS.getId() : ResponseStatus.FAILURE.getId());
    }

    public Response(Exception e) {
        this.result = ResponseStatus.SUCCESS.getId();
        this.result = ResponseStatus.FAILURE.getId();
        this.msg = e.getMessage();
    }
}

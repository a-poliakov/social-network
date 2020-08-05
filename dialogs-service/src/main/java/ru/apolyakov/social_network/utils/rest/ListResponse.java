package ru.apolyakov.social_network.utils.rest;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.*;

@Data
@NoArgsConstructor
@ToString
public class ListResponse<T> {
    private List<T> data = new ArrayList<>();

    private String result;
    private String msg;

    public ListResponse(T data) {
        this.data = Collections.singletonList(data);
    }

    public ListResponse(Collection<T> data) {
        this.data = new ArrayList<>(data);
    }

    public ListResponse(T... data) {
        this.data = Arrays.asList(data);
    }

    public ListResponse(Exception e) {
        this.result = ResponseStatus.SUCCESS.getId();
        this.result = ResponseStatus.FAILURE.getId();
        this.msg = e.getMessage();
    }

    public boolean add(T elm) {
        return this.data.add(elm);
    }
}

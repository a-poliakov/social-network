package ru.apolyakov.social_network.utils.rest;

public enum ResponseStatus {
    SUCCESS("success"),
    FAILURE("failure"),
    UNAVAILABLE("unavailable");

    private String id;

    ResponseStatus(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }
}

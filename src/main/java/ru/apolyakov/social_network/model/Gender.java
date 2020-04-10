package ru.apolyakov.social_network.model;

public enum Gender {
    male,
    female;

    public static Gender value(String value) {
        return value == null ? null : Gender.valueOf(value);
    }
}

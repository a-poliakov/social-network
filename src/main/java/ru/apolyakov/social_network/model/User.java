package ru.apolyakov.social_network.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private int id;
    private String login;
    private String password;

    private String firstName;
    private String secondName;

    private int age;

}

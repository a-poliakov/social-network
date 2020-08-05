package ru.apolyakov.social_network.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.apolyakov.social_network.model.Gender;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileDto implements Serializable {
    private List<UserDto> friends;

    private int id;
    private String login;
    private String password;
    private String confirmPassword;

    private String firstName;
    private String secondName;

    private int age;
    private Gender gender;
    private String interests;
    private String city;
}

package ru.apolyakov.social_network.dto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
@Builder
public class WallPostDto implements Serializable {
    private Long id;
    private UserDto fromUser;
    private UserDto toUser;
    private String dateCreated;
    private String text;
}

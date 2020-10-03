package ru.apolyakov.social_network.dto;

import lombok.*;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserWallDto implements Serializable {
    private Long userId;
    private List<WallPostDto> wallPosts = new LinkedList<>();
}

package ru.apolyakov.social_network.service;

import ru.apolyakov.social_network.dto.UserWallDto;
import ru.apolyakov.social_network.model.WallPost;

import java.util.List;

public interface WallPostService {
    UserWallDto getLentaCached(Long userId);

    List<WallPost> getUserLentaPosts(Long userId, Long minId, Long limit);
}

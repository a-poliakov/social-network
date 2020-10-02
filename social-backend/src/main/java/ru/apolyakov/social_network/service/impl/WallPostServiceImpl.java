package ru.apolyakov.social_network.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import ru.apolyakov.social_network.security.SecurityService;
import ru.apolyakov.social_network.dto.UserWallDto;
import ru.apolyakov.social_network.dto.WallPostDto;
import ru.apolyakov.social_network.dto.WallPostToDtoConverter;
import ru.apolyakov.social_network.model.User;
import ru.apolyakov.social_network.model.WallPost;
import ru.apolyakov.social_network.repository.WallPostRepository;
import ru.apolyakov.social_network.service.WallPostService;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static ru.apolyakov.social_network.config.CacheConfiguration.LENTA_CACHE;

@Slf4j
@Component
@RequiredArgsConstructor
public class WallPostServiceImpl implements WallPostService {
    private final WallPostToDtoConverter wallPostToDtoConverter;
    private final WallPostRepository wallPostRepository;
    private final SecurityService securityService;

    @Resource
    private WallPostService self;

    @Cacheable(cacheManager = "redisCacheManager", cacheNames = LENTA_CACHE, key = "#userId")
    public UserWallDto getLentaCached(Long userId) {
        log.info("put lenta to cache for user {}", userId);
        List<WallPostDto> userLentaPosts = getUserLentaPosts(userId, 1L, 1000L).stream()
                .map(wallPostToDtoConverter::convert)
                .collect(Collectors.toList());
        return UserWallDto.builder()
                .userId(userId)
                .wallPosts(userLentaPosts)
                .build();
    }

    public List<WallPost> getUserLentaPosts(Long userId, Long minId, Long limit) {
        return wallPostRepository.getByToUserInWithOffset(userId, minId, limit);
    }

    public List<WallPostDto> getUserLentaPosts(Long userId) {
        if (userId != null) {
            return self.getLentaCached(userId).getWallPosts();
        }

        return securityService.getAuthUser()
                .map(User::getId)
                .map(self::getLentaCached)
                .map(UserWallDto::getWallPosts)
                .orElse(Collections.emptyList());
    }
}

package ru.apolyakov.social_network.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import ru.apolyakov.social_network.dto.UserWallDto;
import ru.apolyakov.social_network.dto.WallPostDto;
import ru.apolyakov.social_network.dto.WallPostToDtoConverter;
import ru.apolyakov.social_network.model.WallPost;
import ru.apolyakov.social_network.service.WallPostService;

import javax.annotation.Resource;

import java.util.List;
import java.util.Optional;

import static ru.apolyakov.social_network.config.CacheConfiguration.LENTA_CACHE;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisCacheService {
    private final WallPostToDtoConverter wallPostToDtoConverter;

    @Qualifier("redisCacheManager")
    @Resource
    private CacheManager cacheManager;

    @Lazy
    @Resource
    private WallPostService wallPostService;

    public void addPostToLenta(Long userId, WallPost wallPost) {
        Cache cache = cacheManager.getCache(LENTA_CACHE);
        Assert.notNull(cache, LENTA_CACHE + " cache is null");
//        UserWallDto userWallDto = cache.get(userId, UserLentaDto.class);
        UserWallDto userWallDto = wallPostService.getLentaCached(userId);

        if (userWallDto == null) {
            userWallDto = new UserWallDto();
            userWallDto.setUserId(userId);
        }

        List<WallPostDto> wallPosts = userWallDto.getWallPosts();
        Optional<Long> first = wallPosts.stream()
                .map(WallPostDto::getId)
                .filter(wallPost.getId()::equals)
                .findFirst();
        if (first.isPresent()) {
            log.warn("Post {} already in cache", wallPost);
            return;
        }

        wallPosts.add(wallPostToDtoConverter.convert(wallPost));
        wallPosts.sort((w1, w2) -> w2.getDateCreated().compareTo(w1.getDateCreated()));

        cache.put(userId, userWallDto);
        log.info("Post added to User({}) cache& Post: {}", userId, wallPost);
    }

    @CacheEvict(cacheNames = LENTA_CACHE, cacheManager = "redisCacheManager")
    public Long evictCache(Long userId) {
        return userId;
    }

}

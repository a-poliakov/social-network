package ru.apolyakov.social_network.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.apolyakov.social_network.model.WallPost;

@Component
@RequiredArgsConstructor
public class WallPostToDtoConverter  implements Converter<WallPost, WallPostDto> {
    private final UserConverter userConverter;

    @Override
    public WallPostDto convert(WallPost wallPost) {
        return WallPostDto.builder()
                .id(wallPost.getId())
                .fromUser(userConverter.convert(wallPost.getFromUser().intValue()))
                .toUser(userConverter.convert(wallPost.getToUser().intValue()))
                .text(wallPost.getBody())
                .dateCreated(wallPost.getDateCreated().toString())
                .build();
    }
}

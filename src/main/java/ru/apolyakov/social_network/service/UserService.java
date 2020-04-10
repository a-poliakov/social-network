package ru.apolyakov.social_network.service;

import org.springframework.security.core.AuthenticationException;
import ru.apolyakov.social_network.dto.ProfileDto;
import ru.apolyakov.social_network.dto.UserDto;

public interface UserService {
    UserDto findUserByLogin(String login);

    Long getCurrentUserId();

    ProfileDto register(ProfileDto profileDto);

    ProfileDto loadProfile() throws AuthenticationException;
}

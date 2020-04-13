package ru.apolyakov.social_network.service;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import ru.apolyakov.social_network.dto.ProfileDto;
import ru.apolyakov.social_network.dto.UserDto;

import java.util.List;

public interface UserService extends UserDetailsService {
    UserDto findUserByLogin(String login);

    Long getCurrentUserId();

    ProfileDto register(ProfileDto profileDto);

    ProfileDto loadProfile() throws AuthenticationException;

    ProfileDto loadProfile(int userId) throws AuthenticationException;

    List<UserDto> loadUsersList() throws AuthenticationException;

    void addFriend(int friendId);
}

package ru.apolyakov.social_network.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.apolyakov.social_network.model.User;
import ru.apolyakov.social_network.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserConverter implements Converter<Integer, UserDto> {
    private final UserRepository userRepository;

    @Override
    public UserDto convert(Integer userId) {
        return userRepository.findById(userId)
                .map(user -> UserDto.builder()
                        .id(userId)
                        .firstName(user.getFirstName())
                        .secondName(user.getSecondName())
                        .login(user.getLogin())
                        .build())
                .orElse(null);
    }

    public static ProfileDto convertToProfile(User user, List<UserDto> friends) {
        return ProfileDto.builder()
                .id((int)user.getId())
                .login(user.getLogin())
                .firstName(user.getFirstName())
                .secondName(user.getSecondName())
                .gender(user.getGender())
                .city(user.getCity())
                .interests(user.getInterests())
                .age(user.getAge())
                .friends(friends)
                .build();

    }

    public static List<UserDto> convertToUserDto(List<User> users) {
        return users == null ? new ArrayList<>() :
                users.stream().map(UserConverter::convertToUserDto).collect(Collectors.toList());
    }

    public static UserDto convertToUserDto(User user) {
        return UserDto.builder()
                .id((int)user.getId())
                .login(user.getLogin())
                .firstName(user.getFirstName())
                .secondName(user.getSecondName())
                .gender(user.getGender())
                .city(user.getCity())
                .interests(user.getInterests())
                .age(user.getAge())
                .build();

    }

    public static User convertToUser(ProfileDto profileDto, String encrytedPassword) {
        return User.builder()
                .login(profileDto.getLogin())
                .password(encrytedPassword)
                .firstName(profileDto.getFirstName())
                .secondName(profileDto.getSecondName())
                .gender(profileDto.getGender())
                .city(profileDto.getCity())
                .interests(profileDto.getInterests())
                .age(profileDto.getAge())
                .build();
    }
}

package ru.apolyakov.social_network.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.apolyakov.social_network.dto.ProfileDto;
import ru.apolyakov.social_network.dto.UserConverter;
import ru.apolyakov.social_network.dto.UserDto;
import ru.apolyakov.social_network.model.User;
import ru.apolyakov.social_network.repository.UserRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl  implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user = userRepository.findByLogin(s).orElseThrow(() -> new UsernameNotFoundException("User " + s + " not found!"));
        return new org.springframework.security.core.userdetails.User(user.getLogin(), user.getPassword(), new HashSet<>());
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null &&  authentication.getPrincipal() != null && !isAnonimous(authentication)) {
            org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
            Optional<User> byLogin = userRepository.findByLogin(principal.getUsername());
            return byLogin.isPresent() ? byLogin.get().getId() : null;
        }
        return null;
    }

    private boolean isAnonimous(Authentication authentication) {
        return !authentication.isAuthenticated() || authentication.getPrincipal() instanceof String && authentication.getPrincipal().equals("anonymousUser");
    }

    @Override
    @Transactional(readOnly = false)
    public ProfileDto register(ProfileDto profileDto) {
        String encrytedPassword = passwordEncoder.encode(profileDto.getPassword());
        User newUser = UserConverter.convertToUser(profileDto, encrytedPassword);
        newUser = userRepository.save(newUser);
        //Optional<User> savedUser = userDao.findByLogin(profileDto.getLogin());
        //savedUser.ifPresent(user -> profileDto.setId(user.getId()));
        profileDto.setId(newUser.getId());
        return profileDto;
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileDto loadProfile() throws AuthenticationException {
        Optional<User> currentUser = userRepository.findById(getCurrentUserId());
        if(currentUser.isPresent()) {
            List<User> friends = userRepository.findFriends((long) currentUser.get().getId());
            return UserConverter.convertToProfile(currentUser.get(), UserConverter.convertToUserDto(friends).stream().peek(dto -> dto.setFriend(Boolean.TRUE)).collect(Collectors.toList()));
        }
        throw new UsernameNotFoundException("User hasn't logged in!");
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileDto loadProfile(int userId) throws AuthenticationException {
        Optional<User> currentUser = userRepository.findById(userId);
        if(currentUser.isPresent()) {
            List<User> friends = userRepository.findFriends((long) currentUser.get().getId());
            return UserConverter.convertToProfile(currentUser.get(), UserConverter.convertToUserDto(friends).stream().peek(dto -> dto.setFriend(Boolean.TRUE)).collect(Collectors.toList()));
        }
        throw new UsernameNotFoundException("User hasn't found!");
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> loadUsersList() throws AuthenticationException {
        Optional<User> currentUser = userRepository.findById(getCurrentUserId());
        if(currentUser.isPresent()) {
            Set<Integer> friendsIds = userRepository.findFriends((long) currentUser.get().getId())
                    .stream()
                    .map(User::getId)
                    .collect(Collectors.toSet());
            return UserConverter.convertToUserDto(userRepository.findAll())
                    .stream()
                    .filter(user -> currentUser.get().getId() != user.getId())
                    .peek(user -> user.setFriend(friendsIds.contains(user.getId())))
                    .collect(Collectors.toList());
        }
        throw new UsernameNotFoundException("User hasn't logged in!");
    }

    @Override
    @Transactional(readOnly = false)
    public void addFriend(int friendId) {
        Optional<User> currentUser = userRepository.findById(getCurrentUserId());
        if(currentUser.isPresent()) {
            Set<Integer> friendsIds = userRepository.findFriends((long) currentUser.get().getId())
                    .stream()
                    .map(User::getId)
                    .collect(Collectors.toSet());
            if (!friendsIds.contains(friendId)) {
                userRepository.addFriendRelation(currentUser.get().getId(), friendId);
            }
            friendsIds = userRepository.findFriends((long) friendId)
                    .stream()
                    .map(User::getId)
                    .collect(Collectors.toSet());
            if (!friendsIds.contains(currentUser.get().getId())) {
                userRepository.addFriendRelation(friendId, currentUser.get().getId());
            }
            return;
        }
        throw new UsernameNotFoundException("User hasn't logged in!");
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto findUserByLogin(String login) {
        try {
            User user = userRepository.findByLogin(login).orElse(null);
            return user == null ? null : UserConverter.convertToUserDto(user);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> searchByFirstAndSecondName(String firstNamePattern, String secondNamePattern) {
        List<Optional<User>> optionalList = userRepository.findUserLikeFirstNameAndLikeSecondName("'" + firstNamePattern + "%'", "'" + secondNamePattern + "%'");
        return optionalList.stream()
                .filter(Optional::isPresent)
                .map(optional -> UserConverter.convertToUserDto(optional.get()))
                .collect(Collectors.toList());
    }
}

package ru.apolyakov.social_network.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.apolyakov.social_network.dao.JdbcUserDao;
import ru.apolyakov.social_network.dto.ProfileDto;
import ru.apolyakov.social_network.dto.UserConverter;
import ru.apolyakov.social_network.dto.UserDto;
import ru.apolyakov.social_network.model.User;

import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Component
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl  implements UserService, UserDetailsService {
    private final JdbcUserDao userDao;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user = userDao.findByLogin(s).orElseThrow(() -> new UsernameNotFoundException("User " + s + " not found!"));
        return new org.springframework.security.core.userdetails.User(user.getLogin(), user.getPassword(), new HashSet<>());
    }

    @Override
    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null &&  authentication.getPrincipal() != null ) {
            org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
            Optional<User> byLogin = userDao.findByLogin(principal.getUsername());
            return byLogin.isPresent() ? (long) byLogin.get().getId() : null;
        }
        return null;
    }

    @Override
    public ProfileDto register(ProfileDto profileDto) {
        String encrytedPassword = passwordEncoder.encode(profileDto.getPassword());
        User newUser = UserConverter.convertToUser(profileDto, encrytedPassword);
        int save = userDao.save(newUser);

        Optional<User> savedUser = userDao.findByLogin(profileDto.getLogin());
        savedUser.ifPresent(user -> profileDto.setId(user.getId()));
        return profileDto;
    }

    @Override
    public ProfileDto loadProfile() throws AuthenticationException {
        Optional<User> currentUser = userDao.findById(getCurrentUserId());
        if(currentUser.isPresent()) {
            List<User> friends = userDao.findFriends((long) currentUser.get().getId());
            return UserConverter.convertToProfile(currentUser.get(), UserConverter.convertToUserDto(friends));
        }
        throw new UsernameNotFoundException("User hasn't logged in!");
    }

    @Override
    public UserDto findUserByLogin(String login) {
        try {
            User user = userDao.findByLogin(login).orElse(null);
            return user == null ? null : UserConverter.convertToUserDto(user);
        } catch (Exception e) {
            return null;
        }
    }
}

package ru.apolyakov.social_network.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import ru.apolyakov.social_network.dao.JdbcUserDao;
import ru.apolyakov.social_network.model.User;

import java.util.HashSet;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl  implements UserService, UserDetailsService {
    private final JdbcUserDao userDao;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user = userDao.getByLogin(s);
        return new org.springframework.security.core.userdetails.User(user.getLogin(), user.getPassword(), new HashSet<>());
    }
}

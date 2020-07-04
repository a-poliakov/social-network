package ru.apolyakov.social_network.service;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.apolyakov.social_network.dto.ProfileDto;
import ru.apolyakov.social_network.dto.UserDto;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TarantoolUserService  {
    private static final String SPACE_NAME = "user_space";

    private final TarantoolClient tarantoolClient;
    private final TupleConverter tupleConverter;
    private final DriverManagerDataSource driverManagerDataSource;

    private NamedParameterJdbcTemplate template;

    @PostConstruct
    public void init() {
        template = new NamedParameterJdbcTemplate(driverManagerDataSource);

    }

    public Optional<User> findUserById(Long id) {
        List<?> user = tarantoolClient.syncOps().select(SPACE_NAME, "primary", Collections.singletonList(id), 0, 1, Iterator.EQ);
        return user.stream()
                .map(q -> (List<?>) q)
                .map(tupleConverter::fromTuple)
                .findFirst();
    }

    public UserDto findUserByLogin(String login) {
        List<?> user = tarantoolClient.syncOps().select(SPACE_NAME, "login_idx", Collections.singletonList(login), 0, 1, Iterator.EQ);
        return user.stream()
                .map(q -> (List<?>) q)
                .map(tupleConverter::fromTuple)
                .findFirst();
    }
    
    public List<UserDto> searchByFirstAndSecondName(String firstNamePattern, String secondNamePattern) {
        List<?> user = tarantoolClient.syncOps().call("search_by_first_second_name", "'" + firstNamePattern + "%'", "'" + secondNamePattern + "%'", 100);
        if (user.isEmpty()) {
            return Collections.emptyList();
        }
        List<?> users = (List<?>) user.get(0);
        return users.stream()
                .map(q -> (List<?>) q)
                .map(tupleConverter::fromTuple)
                .collect(Collectors.toList());
    }
}

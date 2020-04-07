package ru.apolyakov.social_network.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.apolyakov.social_network.model.User;

@Component
@RequiredArgsConstructor
public class JdbcUserDao {
    private final JdbcTemplate template;

    public void test() {
        template.execute("SELECT * FROM users");
    }

    public User getByLogin(String login) {
        return null;
    }
}

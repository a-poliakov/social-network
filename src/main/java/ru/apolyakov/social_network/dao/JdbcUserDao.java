package ru.apolyakov.social_network.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import ru.apolyakov.social_network.model.Gender;
import ru.apolyakov.social_network.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JdbcUserDao {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final JdbcTemplate template;

    public void test() {
        template.execute("SELECT * FROM users");
    }

    public Optional<User> findByLogin(String login) {
        return namedParameterJdbcTemplate.queryForObject(
                "select * from users where login = :login",
                new MapSqlParameterSource("login", login),
                (rs, rowNum) ->
                        Optional.of(
                                User.builder()
                                        .id(rs.getInt("id"))
                                        .login(rs.getString("login"))
                                        .password(rs.getString("password"))
                                        .firstName(rs.getString("first_name"))
                                        .secondName(rs.getString("second_name"))
                                        .gender(Gender.value(rs.getString("sex")))
                                        .city(rs.getString("city"))
                                        .interests(rs.getString("interests"))
                                        .age(rs.getInt("age"))
                                        .build()
                        ));
    }

    public Optional<User> findById(Long id) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource("id", id);
        return namedParameterJdbcTemplate.queryForObject(
                "select * from users where id = :id",
                parameterSource,
                (rs, rowNum) ->
                        Optional.of(
                                User.builder()
                                        .id(rs.getInt("id"))
                                        .login(rs.getString("login"))
                                        .password(rs.getString("password"))
                                        .firstName(rs.getString("first_name"))
                                        .secondName(rs.getString("second_name"))
                                        .gender(Gender.value(rs.getString("sex")))
                                        .city(rs.getString("city"))
                                        .interests(rs.getString("interests"))
                                        .age(rs.getInt("age"))
                                        .build()
                        ));
    }

    public List<User> findFriends(Long id) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource("id", id);
        return namedParameterJdbcTemplate.query(
                "select * from users where id in (select friend_id from user_subscription where user_id = :id)",
                parameterSource,
                //new SingleColumnRowMapper(Integer.class)
                (rs, rowNum) -> User.builder()
                                        .id(rs.getInt("id"))
                                        .login(rs.getString("login"))
                                        .firstName(rs.getString("first_name"))
                                        .secondName(rs.getString("second_name"))
                                        .gender(Gender.value(rs.getString("sex")))
                                        .city(rs.getString("city"))
                                        .interests(rs.getString("interests"))
                                        .age(rs.getInt("age"))
                                        .build());
    }

    public int update(User user) {
        return namedParameterJdbcTemplate.update(
                "update users " +
                        "set age = :age, login = :login, password = :password, first_name = :first_name, second_name = :second_name, " +
                        "sex = :sex, age = :age, interests = :interests, city = :city " +
                        "where id = :id",
                new BeanPropertySqlParameterSource(user));
    }

    public int count() {
        return template.queryForObject("select count(*) from users", Integer.class);
    }

    public int save(User user) {
        return template.update(
                "insert into social_network.users (login, password, first_name, second_name, sex, age, interests, city) " +
                        "values(?,?,?,?,?,?,?,?)",
                user.getLogin(), user.getPassword(), user.getFirstName(), user.getSecondName(), user.getGender().name(),
                user.getAge(), user.getInterests(), user.getCity());
    }

    public int deleteById(Long id) {
        return template.update(
                "delete users where id = ?",
                id);
    }

    public List<User> findAll() {
        return template.query(
                "select * from users",
                (rs, rowNum) ->
                        User.builder()
                                .id(rs.getInt("id"))
                                .login(rs.getString("login"))
                                .password(rs.getString("password"))
                                .firstName(rs.getString("first_name"))
                                .secondName(rs.getString("second_name"))
                                .gender(Gender.value(rs.getString("sex")))
                                .city(rs.getString("city"))
                                .interests(rs.getString("interests"))
                                .age(rs.getInt("age"))
                                .build()
        );
    }
}

package ru.apolyakov.social_network.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import ru.apolyakov.social_network.model.Gender;
import ru.apolyakov.social_network.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JdbcUserDao {
    private static class UserMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet resultSet, int rowNum) throws SQLException {
            return
                    User.builder()
                            .id(resultSet.getInt("id"))
                            .login(resultSet.getString("login"))
                            .password(resultSet.getString("password"))
                            .firstName(resultSet.getString("first_name"))
                            .secondName(resultSet.getString("second_name"))
                            .gender(Gender.value(resultSet.getString("sex")))
                            .city(resultSet.getString("city"))
                            .interests(resultSet.getString("interests"))
                            .age(resultSet.getInt("age"))
                            .build();
        }
    }

    private static class OptionalUserMapper implements RowMapper<Optional<User>>{
        private final UserMapper userMapper = new UserMapper();

        @Override
        public Optional<User> mapRow(ResultSet resultSet, int rowNum) throws SQLException {
            return Optional.ofNullable(userMapper.mapRow(resultSet, rowNum));
        }
    }

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final JdbcTemplate template;

    public Optional<User> findByLogin(String login) {
        return namedParameterJdbcTemplate.queryForObject(
                "select * from users where login = :login",
                new MapSqlParameterSource("login", login),
                new OptionalUserMapper());
    }

    public List<Optional<User>> findUserLikeFirstNameAndLikeSecondName(String firstName, String secondName) {
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("firstName", firstName + "%");
        mapSqlParameterSource.addValue("secondName", secondName + "%");
        return namedParameterJdbcTemplate.query(
                "select * from users where first_name LIKE :firstName AND second_name LIKE :secondName",
                mapSqlParameterSource,
                new OptionalUserMapper());
    }

    public Optional<User> findById(Long id) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource("id", id);
        try {
            return namedParameterJdbcTemplate.queryForObject(
                    "select * from users where id = :id",
                    parameterSource,
                    new OptionalUserMapper());
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<User> findFriends(Long id) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource("id", id);
        return namedParameterJdbcTemplate.query(
                "select * from users where id in (select friend_id from user_subscription where user_id = :id)",
                parameterSource,
                //new SingleColumnRowMapper(Integer.class)
                new UserMapper());
    }

    public void addFriendRelation(int firstId, int secondId) {
        template.update(
                "insert into user_subscription (user_id, friend_id) values(?,?)", firstId, secondId);
    }

    public int update(User user) {
        return namedParameterJdbcTemplate.update(
                "update users " +
                        "set age = :age, login = :login, password = :password, first_name = :first_name, second_name = :second_name, " +
                        "sex = :sex, age = :age, interests = :interests, city = :city " +
                        "where id = :id",
                new BeanPropertySqlParameterSource(user));
    }

    public Integer count() {
        return template.queryForObject("select count(*) from users", Integer.class);
    }

    public int save(User user) {
        return template.update(
                "insert into users (login, password, first_name, second_name, sex, age, interests, city) " +
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
               new UserMapper()
        );
    }
}

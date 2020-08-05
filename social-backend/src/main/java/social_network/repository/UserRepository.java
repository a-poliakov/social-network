package ru.apolyakov.social_network.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.apolyakov.social_network.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query(
            value = "SELECT * FROM users u WHERE u.login = :login",
            nativeQuery = true)
    Optional<User> findByLogin(@Param("login") String login);

    @Query(
            value = "select * from users where first_name LIKE :firstName AND second_name LIKE :secondName",
            nativeQuery = true)
    List<Optional<User>> findUserLikeFirstNameAndLikeSecondName(@Param("firstName") String firstName,
                                                                @Param("secondName") String secondName);


    Optional<User> findById(Integer id);

    @Query(
            value = "select * from users where id in (select friend_id from user_subscription where user_id = :id)",
            nativeQuery = true)
    List<User> findFriends(@Param("id") Long id);

    @Modifying(clearAutomatically = true)
    @Query(value = "insert into user_subscription (user_id, friend_id) values (:firstId,:secondId)",
            nativeQuery = true)
    void addFriendRelation(@Param("firstId") int firstId, @Param("secondId") int secondId);
}

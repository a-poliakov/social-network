package ru.apolyakov.social_network.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import ru.apolyakov.social_network.model.Chat;

import java.util.List;

@Repository
public interface ChatRepository extends MongoRepository<Chat, String> {

    List<Chat> findByLabel(String label);

    @Query(value="{fromUser: ?0}",
            sort="{'createdAt': -1}")
    List<Chat> findByFromUser(Long fromUser);
}

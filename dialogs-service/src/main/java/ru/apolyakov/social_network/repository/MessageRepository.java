package ru.apolyakov.social_network.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import ru.apolyakov.social_network.model.Message;

import java.time.LocalDate;
import java.util.stream.Stream;

@Repository
public interface MessageRepository extends MongoRepository<Message, String> {

    @Query(value="{chatId: ?0, createdAt: {'$gt': ?1}}",
            sort="{'createdAt': -1}")
    Stream<Message> getByChatIdAndDateCreatedAfter(String chatId, LocalDate dateCreated);
}

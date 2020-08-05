package ru.apolyakov.social_network.model;

import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@EqualsAndHashCode(of = "id")
@Document
@CompoundIndexes({
        @CompoundIndex(name = "chatId_createdAt", def = "{'chatId' : 1, 'createdAt': -1}")
})
public class Message {
    @Id
    private String id;

    private String chatId;

    private Long createdAt;

    private Long fromId;

    private String messageBody;
}

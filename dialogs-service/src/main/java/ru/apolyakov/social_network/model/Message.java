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
@Accessors(chain = true)
@EqualsAndHashCode(of = "id")
@Document
@CompoundIndexes({
        @CompoundIndex(name = "chatId_dateCreated", def = "{'chatId' : 1, 'dateCreated': -1}")
})
public class Message {
    @Id
    private String id;

    private String chatId;

    private Long dateCreated;

    private Long fromId;

    private String textMessage;
}

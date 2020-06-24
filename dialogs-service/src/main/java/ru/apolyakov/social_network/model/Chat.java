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
        @CompoundIndex(name = "fromUser_dateCreated", def = "{'fromUser' : 1, 'dateCreated': -1}")
})
public class Chat {
    @Id
    private String id;

    //todo: group chats
    private Long fromUser;
    private Long toUser;

    private String label;

    private Long dateCreated;
}

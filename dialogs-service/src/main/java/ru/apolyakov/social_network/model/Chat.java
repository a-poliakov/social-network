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
        @CompoundIndex(name = "fromUser_createdAt", def = "{'fromUserId' : 1, 'createdAt': -1}")
})
public class Chat {
    @Id
    private String id;

    //todo: group chats
    private Long fromUserId;
    private Long toUserId;

    private String label;

    private Long createdAt;
}

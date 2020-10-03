package ru.apolyakov.social_network.service.chats.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ChatDto {
    private String id;

    private Long fromUserId;
    private Long toUserId;

    private String label;

    private Long createdAt;
}

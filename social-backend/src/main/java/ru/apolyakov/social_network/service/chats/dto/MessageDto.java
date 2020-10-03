package ru.apolyakov.social_network.service.chats.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class MessageDto {
    private String id;

    private String chatId;

    private Long createdAt;

    private Long fromId;

    private String messageBody;
}

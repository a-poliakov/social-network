package ru.apolyakov.social_network.service;

import ru.apolyakov.social_network.model.Chat;

import java.util.List;

public interface ChatService {
    List<Chat> getUserChats(Long userId);

    Chat createChat(Long fromUser, Long toUser, Long date, String label);
}

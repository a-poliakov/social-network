package ru.apolyakov.social_network.service;

import ru.apolyakov.social_network.model.Message;

import java.util.List;

public interface MessageService {
    List<Message> getMessages(String chatId, String fromDate, Integer count);

    Message createMessage(String chatId, Long fromId, Long date, String text);
}

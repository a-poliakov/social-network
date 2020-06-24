package ru.apolyakov.social_network.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.apolyakov.social_network.model.Chat;
import ru.apolyakov.social_network.repository.ChatRepository;
import ru.apolyakov.social_network.service.ChatService;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
    private final ChatRepository chatRepository;

    public List<Chat> getUserChats(Long userId) {
        return chatRepository.findByFromUser(userId);
    }

    public Chat createChat(Long fromUser, Long toUser, Long date, String label) {
        Chat chat = new Chat()
                .setFromUserId(fromUser)
                .setToUserId(toUser)
                .setCreatedAt(date);
        return chatRepository.insert(chat);
    }
}

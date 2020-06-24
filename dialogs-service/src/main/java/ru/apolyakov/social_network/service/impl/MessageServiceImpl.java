package ru.apolyakov.social_network.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.apolyakov.social_network.model.Message;
import ru.apolyakov.social_network.repository.MessageRepository;
import ru.apolyakov.social_network.service.MessageService;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final MessageRepository messageRepository;

    public List<Message> getMessages(String chatId, String fromDate, Integer count) {
        LocalDate dateCreated = DateTimeUtil.parseIsoDate(fromDate);
        return messageRepository.getByChatIdAndDateCreatedAfter(chatId, dateCreated)
                .limit(count)
                .collect(Collectors.toList());
    }

    public Message createMessage(String chatId, Long fromId, Long date, String text) {
        Message messageDoc = new Message()
                .setChatId(chatId)
                .setDateCreated(date)
                .setFromId(fromId)
                .setTextMessage(text);
        return messageRepository.insert(messageDoc);
    }
}

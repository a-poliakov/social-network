package ru.apolyakov.social_network.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.apolyakov.social_network.model.Chat;
import ru.apolyakov.social_network.model.Message;
import ru.apolyakov.social_network.repository.MessageRepository;
import ru.apolyakov.social_network.service.MessageService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final MessageRepository messageRepository;

    public List<Message> getMessages(String chatId, String fromDate, Integer count) {
        LocalDate dateCreated = parseIsoDate(fromDate);
        return messageRepository.getByChatIdAndDateCreatedAfter(chatId, dateCreated)
                .limit(count)
                .collect(Collectors.toList());
    }

    public Message createMessage(String chatId, Long fromId, Long date, String text) {
        Message messageDoc = Message.builder()
                .chatId(chatId)
                .createdAt(date)
                .fromId(fromId)
                .messageBody(text)
                .build();
        return messageRepository.insert(messageDoc);
    }

    @Override
    public Message editMessage(String messageId, String text) {
        Optional<Message> byId = messageRepository.findById(messageId);
        if (byId.isPresent()) {
            Message message = byId.get();
            message.setMessageBody(text);
            messageRepository.save(message);
            return message;
        }
        return null;
    }

    @Override
    public boolean deleteMessage(String messageId) {
        messageRepository.deleteById(messageId);
        return true;
    }

    /**
     * @param strDate ISO_LOCAL_DATE 2016-08-16
     */
    private LocalDate parseIsoDate(String strDate) {
        return LocalDate.parse(strDate);
    }
}

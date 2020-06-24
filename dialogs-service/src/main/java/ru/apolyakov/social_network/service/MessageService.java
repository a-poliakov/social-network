package ru.apolyakov.social_network.service;

import ru.apolyakov.social_network.model.Message;

import java.util.List;

/**
 * Service for manage messages.
 *
 * @author apoliakov
 * @since 20.06.2020
 */
public interface MessageService {
    /**
     * Get messages for given chat
     * @param chatId chat id
     * @param fromDate timestamp start for range
     * @param count messages count
     * @return messages list
     */
    List<Message> getMessages(String chatId, String fromDate, Integer count);

    /**
     * Create new message for given chat
     * @param chatId chat id
     * @param fromId author-user id
     * @param date timestamp created at for message
     * @param text message text
     * @return created message
     */
    Message createMessage(String chatId, Long fromId, Long date, String text);

    /**
     * Edit message
     * @param messageId message id
     * @param text edited message text
     * @return edited message
     */
    Message editMessage(String messageId, String text);

    /**
     * Delete chat
     * @param messageId message id
     * @return delete status (successful or not)
     */
    boolean deleteMessage(String messageId);
}

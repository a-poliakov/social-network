package ru.apolyakov.social_network.service;

import ru.apolyakov.social_network.model.Chat;

import java.util.List;

/**
 * Service for manage chats.
 *
 * @author apoliakov
 * @since 20.06.2020
 */
public interface ChatService {
    /**
     * Get chats list for given user
     * @param userId user id
     * @return chats list
     */
    List<Chat> getUserChats(Long userId);

    /**
     * Create new chat
     * @param fromUser (first) user how created chat
     * @param toUser (second) user
     * @param date creation date (timestamp)
     * @param label chat label
     * @return created chat
     */
    Chat createChat(Long fromUser, Long toUser, Long date, String label);

    /**
     * Change chat title
     * @param chatId chat id
     * @param label new chat label
     * @return edited chat
     */
    Chat changeTitle(String chatId, String label);

    /**
     * Delete chat
     * @param chatId chat id
     * @return delete status (successful or not)
     */
    boolean deleteChat(String chatId);
}

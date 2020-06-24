package ru.apolyakov.social_network.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.apolyakov.social_network.model.Chat;
import ru.apolyakov.social_network.service.ChatService;
import ru.apolyakov.social_network.utils.rest.ListResponse;
import ru.apolyakov.social_network.utils.rest.Response;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chats")
public class ChatRestController {
    private final ChatService chatService;

    @GetMapping("/{userId}}")
    public ListResponse<Chat> getUserChats(@PathVariable("userId") Long userId) {
        List<Chat> userChats = chatService.getUserChats(userId);
        return new ListResponse<>(userChats);
    }

    @PostMapping
    public Response<Chat> createChat(@RequestParam("fromUser") Long fromUser, @RequestParam("toUser") Long toUser,
                                     @RequestParam("date") Long date, @RequestParam("label") String label) {
        Chat userChats = chatService.createChat(fromUser, toUser, date, label);
        return new Response<>(userChats);
    }

    @PutMapping("/chat/{chatId}")
    public Response<Chat> changeChatTitle(@PathVariable("chatId") String chatId, @RequestParam("label") String label) {
        Chat userChats = chatService.changeTitle(chatId, label);
        return new Response<>(userChats);
    }

    @DeleteMapping("/chat/{chatId}")
    public boolean deleteChat(@PathVariable("chatId") String chatId) {
        return chatService.deleteChat(chatId);
    }
}

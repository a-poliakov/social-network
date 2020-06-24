package ru.apolyakov.social_network.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.apolyakov.social_network.model.Chat;
import ru.apolyakov.social_network.service.ChatService;

import javax.xml.ws.Response;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chats")
public class ChatRestController {
    private final ChatService chatService;

    @GetMapping("/user")
    public ListResponse<Chat> getUserChats(@RequestParam("userId") Long userId) {
        List<Chat> userChats = chatService.getUserChats(userId);
        return new ListResponse<>(userChats);
    }

    @PostMapping
    public Response<Chat> createChat(@RequestParam("fromUser") Long fromUser, @RequestParam("toUser") Long toUser,
                                     @RequestParam("date") Long date) {
        Chat userChats = chatService.createChat(fromUser, toUser, date);
        return new Response<>(userChats);
    }

}

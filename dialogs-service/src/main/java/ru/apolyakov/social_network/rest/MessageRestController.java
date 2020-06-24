package ru.apolyakov.social_network.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.apolyakov.social_network.model.Message;
import ru.apolyakov.social_network.service.MessageService;

import javax.xml.ws.Response;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/messages")
public class MessageRestController {
    private final MessageService messageService;

    @GetMapping
    public Response<List<Message>> getMessages(@RequestParam("chatId") String chatId,
                                                @RequestParam("fromDate") String fromDate,
                                                @RequestParam("count") Integer count) {
        List<Message> messages = messageService.getMessages(chatId, fromDate, count);
        return new Response<>(messages);
    }

    @PostMapping
    public Response<Message> createMessage(@RequestParam("chatId") String chatId,
                                           @RequestParam("fromUser") Long fromUser,
                                           @RequestParam("date") Long date,
                                           @RequestParam("text") String text) {
        Message message = messageService.createMessage(chatId, fromUser, date, text);
        return new Response<>(message);
    }
}

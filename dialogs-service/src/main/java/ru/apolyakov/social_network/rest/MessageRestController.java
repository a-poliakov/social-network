package ru.apolyakov.social_network.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.apolyakov.social_network.model.Message;
import ru.apolyakov.social_network.service.MessageService;
import ru.apolyakov.social_network.utils.rest.Response;

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

    @PutMapping("/{messageId}")
    public Response<Message> editMessage(@PathVariable("messageId") String messageId,
                                         @RequestParam("text") String text) {
        Message message = messageService.editMessage(messageId, text);
        return new Response<>(message);
    }

    @DeleteMapping("/{messageId}")
    public boolean deleteMessage(@PathVariable("messageId") String messageId) {
        return messageService.deleteMessage(messageId);
    }
}

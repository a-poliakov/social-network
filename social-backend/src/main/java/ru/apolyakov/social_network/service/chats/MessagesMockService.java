package ru.apolyakov.social_network.service.chats;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.apolyakov.social_network.config.Constants;
import ru.apolyakov.social_network.service.chats.dto.MessageDto;
import ru.apolyakov.social_network.service.discovery.DiscoveryService;

import javax.naming.ServiceUnavailableException;
import java.net.URI;

@Component
@RequiredArgsConstructor
public class MessagesMockService {
    private final RestTemplate restTemplate = new RestTemplate();
    private final DiscoveryService discoveryService;

    /**
     * Отправить новое сообщение в чат
     * @param messageDto новое сообщение в чат
     * @throws ServiceUnavailableException микросервис не доступен
     */
    public String createMessage(MessageDto messageDto) throws ServiceUnavailableException
    {
        URI service = discoveryService.serviceUrl(Constants.ServicesNames.DIALOGS_SERVICE)
                .map(s -> s.resolve(Constants.API_ENDPOINT + "/messages"))
                .orElseThrow(ServiceUnavailableException::new);
        RequestEntity<MessageDto> newMessageDto = new RequestEntity<>(messageDto, HttpMethod.POST, service);
        return restTemplate.postForEntity(service, newMessageDto, String.class)
                .getBody();
    }
}

package ru.apolyakov.social_network.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import ru.apolyakov.social_network.model.User;
import ru.apolyakov.social_network.mq.RabbitChannelHolder;
import ru.apolyakov.social_network.repository.UserRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomLogoutHandler  implements LogoutHandler {
    private final RabbitChannelHolder channelHolder;
    private final SecurityService securityService;
    private final UserRepository userRepository;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        if (authentication != null  && authentication.getPrincipal() != null && !securityService.isAnonimous(authentication)) {
            // close connection to rabbit / unsubscribe
            org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
            User user = userRepository.findByLogin(principal.getUsername()).get();
            log.info("User {} logged out", user.getLogin());

            channelHolder.closeChannel(user);
        }
    }
}

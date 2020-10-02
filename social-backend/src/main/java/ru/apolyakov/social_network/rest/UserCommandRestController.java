package ru.apolyakov.social_network.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.apolyakov.social_network.dto.UserDto;
import ru.apolyakov.social_network.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Slf4j
@RequiredArgsConstructor
public class UserCommandRestController {
    private final UserService userService;

    @GetMapping("/")
    public ResponseEntity<List<UserDto>> searchUser() {
        List<UserDto> userDtos = userService.loadUsersList();
        return new ResponseEntity<>(userDtos, HttpStatus.OK);
    }
}

package ru.apolyakov.social_network.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.apolyakov.social_network.dto.UserDto;
import ru.apolyakov.social_network.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@Slf4j
@RequiredArgsConstructor
public class SearchRestController {
    private final UserService userService;

    @GetMapping("/search")
    public ResponseEntity<List<UserDto>> searchUser(@RequestParam(required = true, name = "first_name") String firstName,
                                                    @RequestParam(required = true, name = "second_name") String secondName) {
        List<UserDto> result = userService.searchByFirstAndSecondName(firstName, secondName);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}

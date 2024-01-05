package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.common.Marker;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserClient client;

    @PostMapping
    public ResponseEntity<Object> add(@Validated(Marker.OnCreate.class) @RequestBody UserDto userDto) {
        log.info("POST \"/users\" body={}", userDto);
        return client.add(userDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAll() {
        log.info("GET \"/users\"");
        return client.getAll();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getById(@PathVariable Long userId) {
        log.info("GET \"/users/{}\"", userId);
        return client.getById(userId);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(@PathVariable Long userId, @RequestBody UserDto userDto) {
        log.info("PATCH \"/users/{}\" body={}", userId, userDto);
        return client.update(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Long userId) {
        log.info("DELETE \"/users/{}\"", userId);
        client.delete(userId);
    }
}
package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.marker.OnCreate;
import ru.practicum.shareit.marker.OnUpdate;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto add(@RequestBody @Validated(OnCreate.class) UserDto userDto) {
        log.info("POST \"/users\" body={}", userDto);
        return userService.add(userDto);
    }

    @GetMapping
    public List<UserDto> findAll() {
        log.info("GET \"/users\"");
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public UserDto findById(@PathVariable Long id) {
        log.info("GET \"/users/{}\"", id);
        return userService.findById(id);
    }

    @PatchMapping("/{id}")
    public UserDto update(@PathVariable Long id,
                          @Validated(OnUpdate.class) @RequestBody UserDto userDto) {
        log.info("PATCH \"/users/{}\" body={}", id, userDto);
        return userService.update(id, userDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        log.info("DELETE \"/users/{}\"", id);
        userService.delete(id);
    }
}
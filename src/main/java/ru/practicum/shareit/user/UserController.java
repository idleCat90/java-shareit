package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.marker.OnCreate;
import ru.practicum.shareit.marker.OnUpdate;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@Validated
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserDto create(@RequestBody @Validated(OnCreate.class) UserDto userDto) {
        log.info("POST \"/users\" body=" + userDto);
        UserDto userToReturn = userService.create(userDto);
        log.debug(userToReturn.toString());
        return userToReturn;
    }

    @GetMapping
    public List<UserDto> getAll() {
        log.info("GET \"/users\"");
        List<UserDto> userList = userService.getAll();
        log.debug(userList.toString());
        return userList;
    }

    @GetMapping("/{id}")
    public UserDto getById(@PathVariable Long id) {
        log.info("GET \"/users/" + id + "\"");
        return userService.getById(id);
    }

    @PatchMapping("/{id}")
    public UserDto update(@PathVariable Long id,
                          @RequestBody @Validated(OnUpdate.class) UserDto user) {
        log.info("PATCH \"/users/" + id + "\" body=" + user);
        user.setId(id);
        UserDto userToReturn = userService.update(user);
        log.debug(userToReturn.toString());
        return userToReturn;
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        log.info("DELETE \"/users/" + id + "\"");
        userService.deleteById(id);
    }
}
package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto create(UserDto user);

    UserDto getById(Long id);

    List<UserDto> getAll();

    UserDto update(UserDto user);

    void deleteById(Long id);
}

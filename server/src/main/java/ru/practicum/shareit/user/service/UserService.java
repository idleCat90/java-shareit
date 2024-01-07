package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.UserDto;

import java.util.List;

public interface UserService {

    UserDto add(UserDto userDto);

    UserDto findById(Long id);

    List<UserDto> findAll();

    UserDto update(Long id, UserDto userDto);

    void delete(Long id);
}

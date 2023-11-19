package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {
    User create(User user);

    void update(User user);

    Optional<User> getById(Long id);

    List<User> getAll();

    void deleteById(Long id);

    boolean isEmailUnique(UserDto user);
}

package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EmailAlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    UserDao userDao;
    UserMapper userMapper;

    public UserServiceImpl(@Qualifier("userDaoImpl") UserDao userDao, UserMapper userMapper) {
        this.userDao = userDao;
        this.userMapper = userMapper;
    }

    @Override
    public UserDto create(UserDto userDto) {
        String email = userDto.getEmail();
        if (!userDao.isEmailUnique(userDto)) {
            throw new EmailAlreadyExistsException("Email \"" + email + "\" is already used");
        }

        User userToCreate = userMapper.toUser(userDto);
        User userCreated = userDao.create(userToCreate);
        return userMapper.toUserDto(userCreated);
    }

    @Override
    public UserDto getById(Long id) {
        User user = userDao.getById(id)
                .orElseThrow(() -> new NotFoundException("User not found by id " + id));
        return userMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getAll() {
        List<User> userList = userDao.getAll();
        return userMapper.toUserDtoList(userList);
    }

    @Override
    public UserDto update(UserDto userDto) {
        Long userId = userDto.getId();
        String email = userDto.getEmail();

        User userToUpdate = userDao.getById(userId)
                .orElseThrow(() -> new NotFoundException("User not found by id " + userId));
        if (!userDao.isEmailUnique(userDto)) {
            throw new EmailAlreadyExistsException("Email \"" + email + "\" is already used");
        }

        userMapper.updateUserByDto(userDto, userToUpdate);
        userDao.update(userToUpdate);
        return userMapper.toUserDto(userToUpdate);
    }

    @Override
    public void deleteById(Long id) {
        userDao.getById(id)
                .orElseThrow(() -> new NotFoundException("User not found by id " + id));
        userDao.deleteById(id);
    }


}

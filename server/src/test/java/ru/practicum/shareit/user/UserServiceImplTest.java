package ru.practicum.shareit.user;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserServiceImpl userService;

    private final UserDto userDto = UserDto.builder()
            .id(1L)
            .name("user")
            .email("user@mail.com")
            .build();

    @Test
    void whenAddUser_thenReturnUserDto() {
        User user = User.builder()
                .id(1L)
                .name("user")
                .email("user@mail.com")
                .build();

        when(userRepository.save(user)).thenReturn(user);

        UserDto actualUserDto = userService.add(userDto);

        assertEquals(userDto, actualUserDto);
        verify(userRepository).save(user);
    }

    @Test
    void updateUser() {
        UserDto userDtoToUpdate = userService.add(userDto);
        Long userId = userDtoToUpdate.getId();

        UserDto updatedFields = UserDto.builder()
                .name("update")
                .email("update@mail.com").build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(UserMapper.toUser(userDtoToUpdate)));
        UserDto updatedUserDto = userService.update(userId, updatedFields);

        assertNotNull(updatedUserDto);
        assertEquals("update", updatedUserDto.getName());
        assertEquals("update@mail.com", updatedUserDto.getEmail());
    }

    @Test
    void whenUserIsFound_thenFindById() {
        Long userId = 1L;
        User expectedUser = User.builder()
                .id(1L)
                .name("user")
                .email("user@mail.com")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));
        UserDto expectedUserDto = UserMapper.toUserDto(expectedUser);

        UserDto actualUserDto = userService.findById(userId);

        assertEquals(expectedUserDto, actualUserDto);
    }

    @Test
    void whenUserNotFound_thenFindByIdReturnsError() {
        Long userId = 0L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> userService.findById(userId));

        assertEquals("No user with id=0 found", notFoundException.getMessage());
    }

    @Test
    void findAll() {
        List<User> expectedUsers = List.of(new User());
        List<UserDto> expectedUserDtos = expectedUsers.stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());

        when(userRepository.findAll()).thenReturn(expectedUsers);

        List<UserDto> actualUserDtos = userService.findAll();

        assertEquals(actualUserDtos.size(), 1);
        assertEquals(actualUserDtos, expectedUserDtos);
    }

    @Test
    void delete() {
        Long userId = 0L;

        userService.delete(userId);

        verify(userRepository, times(1)).deleteById(userId);
    }
}

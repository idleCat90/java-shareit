package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserServiceIT {

    @Autowired
    private UserService userService;

    private final UserDto userDto = UserDto.builder()
            .name("user")
            .email("user@mail.com")
            .build();

    @Test
    void addNewUser() {
        UserDto actualUserDto = userService.add(userDto);

        assertEquals(1L, actualUserDto.getId());
        assertEquals("user", actualUserDto.getName());
        assertEquals("user@mail.com", actualUserDto.getEmail());
    }

    @Test
    void whenUserIdIsNotValid_thenThrowNotFoundException() {
        Long userId = 2L;

        assertThrows(NotFoundException.class, () -> userService.findById(userId));
    }
}

package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;

    @Test
    @SneakyThrows
    void whenUserIsValid_thenCreateUser() {
        UserDto userDto = UserDto.builder()
                .name("user")
                .email("user@mail.com")
                .build();

        when(userService.add(userDto)).thenReturn(userDto);

        String result = mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(userDto), result);
    }

    @Test
    @SneakyThrows
    void whenUserEmailIsNotValid_thenCreateReturnsBadRequest() {
        UserDto userDto = UserDto.builder()
                .name("user")
                .email("mail.com")
                .build();

        when(userService.add(userDto)).thenReturn(userDto);

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).add(userDto);
    }

    @Test
    @SneakyThrows
    void whenUserNameIsNotValid_thenCreateReturnsBadRequest() {
        UserDto userDto = UserDto.builder()
                .name(" ")
                .email("user@mail.com")
                .build();

        when(userService.add(userDto)).thenReturn(userDto);

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).add(userDto);
    }

    @Test
    @SneakyThrows
    void whenUserIsValid_thenUpdateUser() {
        Long userId = 0L;
        UserDto userDto = UserDto.builder()
                .name("update")
                .email("update@mail.com")
                .build();

        when(userService.update(userId, userDto)).thenReturn(userDto);

        String result = mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(userDto), result);
    }

    @Test
    @SneakyThrows
    void whenUserEmailIsNotValid_thenUpdateReturnsBadRequest() {
        Long userId = 0L;
        UserDto userDto = UserDto.builder()
                .name("update")
                .email("update.com")
                .build();

        when(userService.update(userId, userDto)).thenReturn(userDto);

        mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).update(userId, userDto);
    }

    @Test
    @SneakyThrows
    void findById() {
        Long userId = 0L;

        mockMvc.perform(MockMvcRequestBuilders.get("/users/{userId}", userId))
                .andDo(print())
                .andExpect(status().isOk());

        verify(userService).findById(userId);
    }

    @Test
    @SneakyThrows
    void findAll() {
        List<UserDto> expectedUserDtoList = List.of(UserDto.builder()
                .name("user")
                .email("user@mail.com")
                .build());

        when(userService.findAll()).thenReturn(expectedUserDtoList);

        String result = mockMvc.perform(MockMvcRequestBuilders.get("/users"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(expectedUserDtoList), result);
    }

    @Test
    @SneakyThrows
    void deleteById() {
        Long userId = 0L;

        mockMvc.perform(MockMvcRequestBuilders.delete("/users/{userId}", userId))
                .andExpect(status().isOk());

        verify(userService, times(1)).delete(userId);
    }
}

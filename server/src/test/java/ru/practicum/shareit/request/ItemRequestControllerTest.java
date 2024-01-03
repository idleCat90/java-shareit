package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.request.dto.ItemRequestRespDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.item.ItemController.USER_HEADER;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemRequestService itemRequestService;

    private final User user = User.builder()
            .id(1L)
            .name("user")
            .email("user@mail.com")
            .build();

    private final ItemRequestRespDto itemRequestRespDto = ItemRequestRespDto.builder()
            .id(1L)
            .description("description")
            .items(List.of())
            .created(LocalDateTime.now())
            .build();

    @Test
    @SneakyThrows
    void addRequest() {
        when(itemRequestService.add(any(), any())).thenReturn(itemRequestRespDto);

        String result = mockMvc.perform(MockMvcRequestBuilders.post("/requests")
                        .header(USER_HEADER, user.getId())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemRequestRespDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemRequestRespDto), result);
    }

    @Test
    @SneakyThrows
    void findUserRequests() {
        when(itemRequestService.findUserRequests(user.getId())).thenReturn(List.of(itemRequestRespDto));

        String result = mockMvc.perform(MockMvcRequestBuilders.get("/requests")
                        .header(USER_HEADER, user.getId())
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(List.of(itemRequestRespDto)), result);
    }

    @Test
    @SneakyThrows
    void findAllRequests() {
        Integer from = 0;
        Integer size = 10;
        when(itemRequestService.findAllRequests(user.getId(), from, size)).thenReturn(List.of(itemRequestRespDto));

        String result = mockMvc.perform(MockMvcRequestBuilders.get("/requests/all")
                        .header(USER_HEADER, user.getId())
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(List.of(itemRequestRespDto)), result);
    }

    @Test
    @SneakyThrows
    void findRequestById() {
        Long requestId = 1L;
        when(itemRequestService.findRequestById(user.getId(), requestId)).thenReturn(itemRequestRespDto);

        String result = mockMvc.perform(MockMvcRequestBuilders.get("/requests/{requestId}", requestId)
                        .header(USER_HEADER, user.getId())
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemRequestRespDto), result);
    }
}

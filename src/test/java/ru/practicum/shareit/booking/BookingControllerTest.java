package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingReqDto;
import ru.practicum.shareit.booking.dto.BookingRespDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.item.ItemController.USER_HEADER;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BookingService bookingService;

    private final User user = User.builder()
            .id(1L)
            .name("user")
            .email("user@mail.com")
            .build();

    private final Item item = Item.builder()
            .id(1L)
            .name("item")
            .description("description")
            .owner(user)
            .build();

    private final BookingReqDto bookingReqDto = BookingReqDto.builder()
            .itemId(1L)
            .start(LocalDateTime.now().plusDays(1L))
            .end(LocalDateTime.now().plusDays(2L))
            .build();

    private final BookingRespDto bookingRespDto = BookingRespDto.builder()
            .id(1L)
            .start(LocalDateTime.now().plusDays(1L))
            .end(LocalDateTime.now().plusDays(2L))
            .status(BookingStatus.WAITING)
            .booker(UserMapper.toUserDto(user))
            .item(ItemMapper.toItemRespDto(item))
            .build();

    @Test
    @SneakyThrows
    void whenBookingIsValid_thenCreateBooking() {
        when(bookingService.add(user.getId(), bookingReqDto))
                .thenReturn(bookingRespDto);

        String result = mockMvc.perform(post("/bookings")
                        .contentType("application/json")
                        .header(USER_HEADER, user.getId())
                        .content(objectMapper.writeValueAsString(bookingReqDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookingRespDto), result);
    }

    @Test
    @SneakyThrows
    void whenBookingIsNotValid_thenNotCreateBooking() {
        bookingReqDto.setItemId(null);
        bookingReqDto.setStart(null);
        bookingReqDto.setEnd(null);

        when(bookingService.add(user.getId(), bookingReqDto))
                .thenReturn(bookingRespDto);

        mockMvc.perform(post("/bookings")
                        .contentType("application/json")
                        .header(USER_HEADER, user.getId())
                        .content(objectMapper.writeValueAsString(bookingReqDto)))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).add(user.getId(), bookingReqDto);
    }

    @Test
    @SneakyThrows
    void whenPageIsCorrect_thenFindAllReturnsStatusOk() {
        Integer from = 0;
        Integer size = 10;
        String state = "ALL";

        when(bookingService.findAll(user.getId(), BookingState.ALL.toString(), 0, 10))
                .thenReturn(List.of(bookingRespDto));

        String result = mockMvc.perform(get("/bookings")
                        .param("state", state)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .contentType("application/json")
                        .header(USER_HEADER, user.getId()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(List.of(bookingRespDto)), result);
    }

    @Test
    @SneakyThrows
    void whenPageIsWrong_thenFindAllReturnsBadRequest() {
        Integer from = -1;
        Integer size = 10;

        mockMvc.perform(get("/bookings")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .contentType("application/json")
                        .header(USER_HEADER, user.getId()))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).findAll(user.getId(), "ALL", from, size);
    }

    @Test
    @SneakyThrows
    void whenPageIsCorrect_thenFindAllByOwnerIdReturnsStatusOk() {
        Integer from = 0;
        Integer size = 10;
        String state = "ALL";

        when(bookingService.findAllByOwnerId(user.getId(), BookingState.ALL.toString(), 0, 10))
                .thenReturn(List.of(bookingRespDto));

        String result = mockMvc.perform(get("/bookings/owner")
                        .param("state", state)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .contentType("application/json")
                        .header(USER_HEADER, user.getId()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(List.of(bookingRespDto)), result);
    }

    @Test
    @SneakyThrows
    void whenPageIsWrong_thenFindAllByOwnerIdReturnsBadRequest() {
        Integer from = -1;
        Integer size = 10;

        mockMvc.perform(get("/bookings/owner")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .contentType("application/json")
                        .header(USER_HEADER, user.getId()))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).findAllByOwnerId(user.getId(), "ALL", from, size);
    }

    @Test
    @SneakyThrows
    void whenBookingIsValid_thenUpdateBooking() {
        Boolean approved = true;
        Long bookingId = 1L;

        when(bookingService.update(user.getId(), bookingId, approved))
                .thenReturn(bookingRespDto);

        String result = mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .contentType("application/json")
                        .header(USER_HEADER, user.getId())
                        .param("approved", String.valueOf(approved)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookingRespDto), result);
    }

    @Test
    @SneakyThrows
    void whenBookingIsValid_thenFindById() {
        Long bookingId = 1L;

        when(bookingService.findByUserId(user.getId(), bookingId))
                .thenReturn(bookingRespDto);

        String result = mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .contentType("application/json")
                        .header(USER_HEADER, user.getId())).andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookingRespDto), result);
    }
}

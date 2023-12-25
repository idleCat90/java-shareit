package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingRespDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BookingMapperTest {
    private final Booking booking = Booking.builder()
            .id(1L)
            .item(new Item())
            .start(LocalDateTime.now().plusHours(1L))
            .end(LocalDateTime.now().plusHours(2L))
            .booker(User.builder()
                    .id(1L)
                    .name("user")
                    .email("user@mail.com")
                    .build())
            .status(BookingStatus.WAITING)
            .build();

    @Test
    void toBookingRespDto() {
        BookingRespDto actualDto = BookingMapper.toBookingRespDto(booking);

        assertEquals(1L, actualDto.getId());
        assertEquals(1L, actualDto.getBooker().getId());
        assertEquals("user", actualDto.getBooker().getName());
    }
}

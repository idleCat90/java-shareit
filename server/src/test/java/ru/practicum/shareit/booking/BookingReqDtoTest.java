package ru.practicum.shareit.booking;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.dto.BookingReqDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingReqDtoTest {
    @Autowired
    private JacksonTester<BookingReqDto> json;
    private static final String DATE_TIME = "2023-12-13T21:46:00";
    private BookingReqDto bookingReqDto;

    @BeforeEach
    public void init() {
        bookingReqDto = BookingReqDto.builder()
                .itemId(1L)
                .start(LocalDateTime.parse("2023-12-13T21:46:00"))
                .end(LocalDateTime.parse("2023-12-13T21:46:00"))
                .build();
    }

    @Test
    @SneakyThrows
    public void startSerializes() {
        assertThat(json.write(bookingReqDto)).extractingJsonPathStringValue("$.start")
                .isEqualTo(DATE_TIME);
    }

    @Test
    @SneakyThrows
    public void endSerializes() {
        assertThat(json.write(bookingReqDto)).extractingJsonPathStringValue("$.end")
                .isEqualTo(DATE_TIME);
    }
}

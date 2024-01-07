package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingReqDto;
import ru.practicum.shareit.booking.dto.BookingRespDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemReqDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BookingServiceIT {

    @Autowired
    private BookingService bookingService;
    @Autowired
    private UserService userService;
    @Autowired
    private ItemService itemService;

    private final UserDto userDto1 = UserDto.builder()
            .name("user1")
            .email("user1@mail.com")
            .build();

    private final UserDto userDto2 = UserDto.builder()
            .name("user2")
            .email("user2@mail.com")
            .build();

    private final ItemReqDto itemReqDto1 = ItemReqDto.builder()
            .name("item1")
            .description("item1 description")
            .available(true)
            .build();

    private final ItemReqDto itemReqDto2 = ItemReqDto.builder()
            .name("item2")
            .description("item2 description")
            .available(true)
            .build();

    private final BookingReqDto bookingReqDto1 = BookingReqDto.builder()
            .itemId(2L)
            .start(LocalDateTime.now().plusSeconds(10L))
            .end(LocalDateTime.now().plusSeconds(11L))
            .build();

    @Test
    void addBooking() {
        UserDto addedUser1 = userService.add(userDto1);
        UserDto addedUser2 = userService.add(userDto2);
        itemService.add(addedUser1.getId(), itemReqDto1);
        itemService.add(addedUser2.getId(), itemReqDto2);

        BookingRespDto bookingRespDto1 = bookingService.add(addedUser1.getId(), bookingReqDto1);
        BookingRespDto bookingRespDto2 = bookingService.add(addedUser1.getId(), bookingReqDto1);

        assertEquals(1L, bookingRespDto1.getId());
        assertEquals(2L, bookingRespDto2.getId());
        assertEquals(BookingStatus.WAITING, bookingRespDto1.getStatus());
        assertEquals(BookingStatus.WAITING, bookingRespDto2.getStatus());

        BookingRespDto updatedBookingRespDto1 = bookingService
                .update(addedUser2.getId(), bookingRespDto1.getId(), true);
        BookingRespDto updatedBookingRespDto2 = bookingService
                .update(addedUser2.getId(), bookingRespDto2.getId(), true);

        assertEquals(BookingStatus.APPROVED, updatedBookingRespDto1.getStatus());
        assertEquals(BookingStatus.APPROVED, updatedBookingRespDto2.getStatus());

        List<BookingRespDto> bookingRespDtoList = bookingService.findAllByOwnerId(addedUser2.getId(),
                BookingState.ALL.toString(), 0, 10);

        assertEquals(2, bookingRespDtoList.size());
    }

    @Test
    void whenIdIsInvalid_thenThrowNotFoundException() {
        Long userId = 5L;
        Long bookingId = 5L;

        assertThrows(NotFoundException.class, () -> bookingService.update(userId, bookingId, true));
    }
}

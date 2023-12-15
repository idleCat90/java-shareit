package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingReqDto;
import ru.practicum.shareit.booking.dto.BookingRespDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {

    @Mock
    private UserService userService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingRepository bookingRepository;
    @InjectMocks
    private BookingServiceImpl bookingService;

    private final User user = User.builder()
            .id(1L)
            .name("user")
            .email("user@mail.com")
            .build();

    private final User owner = User.builder()
            .id(2L)
            .name("owner")
            .email("owner@mail.com")
            .build();

    private final UserDto userDto = UserDto.builder()
            .id(1L)
            .name("user")
            .email("user@mail.com")
            .build();

    private final Item item = Item.builder()
            .id(1L)
            .name("item")
            .description("description")
            .available(true)
            .owner(owner)
            .build();

    private final Booking booking = Booking.builder()
            .id(1L)
            .item(item)
            .start(LocalDateTime.now().plusDays(1L))
            .end(LocalDateTime.now().plusDays(2L))
            .booker(user)
            .status(BookingStatus.APPROVED)
            .build();

    private final Booking waitingBooking = Booking.builder()
            .id(1L)
            .item(item)
            .start(LocalDateTime.now().plusDays(1L))
            .end(LocalDateTime.now().plusDays(2L))
            .booker(user)
            .status(BookingStatus.WAITING)
            .build();

    private final BookingReqDto bookingReqDto = BookingReqDto.builder()
            .itemId(1L)
            .start(LocalDateTime.now().plusDays(1L))
            .end(LocalDateTime.now().plusDays(2L))
            .build();

    private final BookingReqDto bookingReqDtoStartAfterEnd = BookingReqDto.builder()
            .itemId(1L)
            .start(LocalDateTime.now().plusDays(1L))
            .end(LocalDateTime.now().minusDays(1L))
            .build();

    @Test
    void whenBookingIsValid_thenCreateBooking() {
        BookingRespDto expectedBookingRespDto = BookingMapper
                .toBookingRespDto(BookingMapper.toBooking(user, item, bookingReqDto));
        when(userService.findById(userDto.getId())).thenReturn(userDto);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class))).thenReturn(BookingMapper.toBooking(user, item, bookingReqDto));

        BookingRespDto actualBookingRespDto = bookingService.add(userDto.getId(), bookingReqDto);

        assertEquals(expectedBookingRespDto, actualBookingRespDto);
    }

    @Test
    void whenStartAfterEnd_thenThrowsValidationException() {
        when(userService.findById(userDto.getId())).thenReturn(userDto);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        ValidationException bookingValidationException = assertThrows(ValidationException.class,
                () -> bookingService.add(userDto.getId(), bookingReqDtoStartAfterEnd));

        assertEquals("Booking must end after start", bookingValidationException.getMessage());
    }

    @Test
    void whenItemNotAvailable_thenCreateThrowsValidationException() {
        item.setAvailable(false);
        when(userService.findById(userDto.getId())).thenReturn(userDto);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        ValidationException bookingValidationException = assertThrows(ValidationException.class,
                () -> bookingService.add(userDto.getId(), bookingReqDto));

        assertEquals("Item not available for booking", bookingValidationException.getMessage());
    }

    @Test
    void whenOwnerIsBooker_thenCreateThrowsNotFoundException() {
        item.setOwner(user);
        when(userService.findById(userDto.getId())).thenReturn(userDto);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> bookingService.add(userDto.getId(), bookingReqDto));

        assertEquals("Item not found", notFoundException.getMessage());
    }

    @Test
    void whenApproved_thenUpdate() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(waitingBooking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(waitingBooking);

        BookingRespDto actualBookingRespDto = bookingService.update(owner.getId(), waitingBooking.getId(), true);

        assertEquals(BookingStatus.APPROVED, actualBookingRespDto.getStatus());
    }

    @Test
    void whenNotApproved_thenUpdateWithRejected() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(waitingBooking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(waitingBooking);

        BookingRespDto actualBookingRespDto = bookingService.update(owner.getId(), waitingBooking.getId(), false);

        assertEquals(BookingStatus.REJECTED, actualBookingRespDto.getStatus());
    }

    @Test
    void whenStatusNotWaiting_thenUpdateThrowsValidationException() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        ValidationException validationException = assertThrows(ValidationException.class,
                () -> bookingService.update(owner.getId(), booking.getId(), false));

        assertEquals("Booking has no WAITING status", validationException.getMessage());
    }

    @Test
    void whenBookingIsValid_thenFindById() {
        BookingRespDto expectedBookingRespDto = BookingMapper.toBookingRespDto(booking);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        BookingRespDto actualBookingRespDto = bookingService.findByUserId(user.getId(), booking.getId());

        assertEquals(expectedBookingRespDto, actualBookingRespDto);
    }

    @Test
    void whenBookingNotValid_thenThrowNotFoundException() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> bookingService.findByUserId(1L, booking.getId()));

        assertEquals("Booking with id=" + booking.getId() + " not found", notFoundException.getMessage());
    }

    @Test
    void whenUserIsNotOwner_thenThrowNotFoundException() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> bookingService.findByUserId(3L, booking.getId()));

        assertEquals("User with id=3 is not the booker or the owner", notFoundException.getMessage());
    }

    @Test
    void whenBookingStateAll_thenFindAllByBookerId() {
        List<BookingRespDto> expectedBookingRespDtoList = List.of(BookingMapper.toBookingRespDto(booking));
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(bookingRepository.findAllBookingsByBookerId(anyLong(), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingRespDto> actualBookingRespDtoList = bookingService.findAll(user.getId(), "ALL", 0, 10);

        assertEquals(expectedBookingRespDtoList, actualBookingRespDtoList);
    }

    @Test
    void whenBookingStateCurrent_thenFindAllByBookerId() {
        List<BookingRespDto> expectedBookingRespDtoList = List.of(BookingMapper.toBookingRespDto(booking));
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(bookingRepository.findAllCurrentBookingsByBookerId(anyLong(),
                any(LocalDateTime.class), any(Pageable.class))).thenReturn(List.of(booking));

        List<BookingRespDto> actualBookingRespDtoList = bookingService.findAll(user.getId(), "CURRENT", 0, 10);

        assertEquals(expectedBookingRespDtoList, actualBookingRespDtoList);
    }

    @Test
    void whenBookingStatePast_thenFindAllByBookerId() {
        List<BookingRespDto> expectedBookingRespDtoList = List.of(BookingMapper.toBookingRespDto(booking));
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(bookingRepository.findAllPastBookingsByBookerId(anyLong(),
                any(LocalDateTime.class), any(Pageable.class))).thenReturn(List.of(booking));

        List<BookingRespDto> actualBookingRespDtoList = bookingService.findAll(user.getId(), "PAST", 0, 10);

        assertEquals(expectedBookingRespDtoList, actualBookingRespDtoList);
    }

    @Test
    void whenBookingStateFuture_thenFindAllByBookerId() {
        List<BookingRespDto> expectedBookingRespDtoList = List.of(BookingMapper.toBookingRespDto(booking));
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(bookingRepository.findAllFutureBookingsByBookerId(anyLong(),
                any(LocalDateTime.class), any(Pageable.class))).thenReturn(List.of(booking));

        List<BookingRespDto> actualBookingRespDtoList = bookingService.findAll(user.getId(), "FUTURE", 0, 10);

        assertEquals(expectedBookingRespDtoList, actualBookingRespDtoList);
    }

    @Test
    void whenBookingStateWaiting_thenFindAllByBookerId() {
        List<BookingRespDto> expectedBookingRespDtoList = List.of(BookingMapper.toBookingRespDto(booking));
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(bookingRepository.findAllWaitingBookingsByBookerId(anyLong(),
                any(LocalDateTime.class), any(Pageable.class))).thenReturn(List.of(booking));

        List<BookingRespDto> actualBookingRespDtoList = bookingService.findAll(user.getId(), "WAITING", 0, 10);

        assertEquals(expectedBookingRespDtoList, actualBookingRespDtoList);
    }

    @Test
    void whenBookingStateRejected_thenFindAllByBookerId() {
        List<BookingRespDto> expectedBookingRespDtoList = List.of(BookingMapper.toBookingRespDto(booking));
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(bookingRepository.findAllRejectedBookingsByBookerId(anyLong(),
                any(LocalDateTime.class), any(Pageable.class))).thenReturn(List.of(booking));

        List<BookingRespDto> actualBookingRespDtoList = bookingService.findAll(user.getId(), "REJECTED", 0, 10);

        assertEquals(expectedBookingRespDtoList, actualBookingRespDtoList);
    }

    @Test
    void whenBookingStateInvalid_thenThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> bookingService.findAll(user.getId(), "HELLO", 0, 10));
        assertThrows(IllegalArgumentException.class,
                () -> bookingService.findAllByOwnerId(user.getId(), "HELLO", 0, 10));
    }

    @Test
    void whenBookingStateAll_thenFindAllByOwnerId() {
        List<BookingRespDto> expectedBookingRespDtoList = List.of(BookingMapper.toBookingRespDto(booking));
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(bookingRepository.findAllBookingsByOwnerId(anyLong(), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingRespDto> actualBookingRespDtoList = bookingService
                .findAllByOwnerId(user.getId(), "ALL", 0, 10);

        assertEquals(expectedBookingRespDtoList, actualBookingRespDtoList);
    }

    @Test
    void whenBookingStateCurrent_thenFindAllByOwnerId() {
        List<BookingRespDto> expectedBookingRespDtoList = List.of(BookingMapper.toBookingRespDto(booking));
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(bookingRepository.findAllCurrentBookingsByOwnerId(anyLong(),
                any(LocalDateTime.class), any(Pageable.class))).thenReturn(List.of(booking));

        List<BookingRespDto> actualBookingRespDtoList = bookingService
                .findAllByOwnerId(user.getId(), "CURRENT", 0, 10);

        assertEquals(expectedBookingRespDtoList, actualBookingRespDtoList);
    }

    @Test
    void whenBookingStatePast_thenFindAllByOwnerId() {
        List<BookingRespDto> expectedBookingRespDtoList = List.of(BookingMapper.toBookingRespDto(booking));
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(bookingRepository.findAllPastBookingsByOwnerId(anyLong(),
                any(LocalDateTime.class), any(Pageable.class))).thenReturn(List.of(booking));

        List<BookingRespDto> actualBookingRespDtoList = bookingService
                .findAllByOwnerId(user.getId(), "PAST", 0, 10);

        assertEquals(expectedBookingRespDtoList, actualBookingRespDtoList);
    }

    @Test
    void whenBookingStateFuture_thenFindAllByOwnerId() {
        List<BookingRespDto> expectedBookingRespDtoList = List.of(BookingMapper.toBookingRespDto(booking));
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(bookingRepository.findAllFutureBookingsByOwnerId(anyLong(),
                any(LocalDateTime.class), any(Pageable.class))).thenReturn(List.of(booking));

        List<BookingRespDto> actualBookingRespDtoList = bookingService
                .findAllByOwnerId(user.getId(), "FUTURE", 0, 10);

        assertEquals(expectedBookingRespDtoList, actualBookingRespDtoList);
    }

    @Test
    void whenBookingStateWaiting_thenFindAllByOwnerId() {
        List<BookingRespDto> expectedBookingRespDtoList = List.of(BookingMapper.toBookingRespDto(booking));
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(bookingRepository.findAllWaitingBookingsByOwnerId(anyLong(),
                any(LocalDateTime.class), any(Pageable.class))).thenReturn(List.of(booking));

        List<BookingRespDto> actualBookingRespDtoList = bookingService
                .findAllByOwnerId(user.getId(), "WAITING", 0, 10);

        assertEquals(expectedBookingRespDtoList, actualBookingRespDtoList);
    }

    @Test
    void whenBookingStateRejected_thenFindAllByOwnerId() {
        List<BookingRespDto> expectedBookingRespDtoList = List.of(BookingMapper.toBookingRespDto(booking));
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(bookingRepository.findAllRejectedBookingsByOwnerId(anyLong(),
                any(Pageable.class))).thenReturn(List.of(booking));

        List<BookingRespDto> actualBookingRespDtoList = bookingService
                .findAllByOwnerId(user.getId(), "REJECTED", 0, 10);

        assertEquals(expectedBookingRespDtoList, actualBookingRespDtoList);
    }
}

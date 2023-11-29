package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingReqDto;
import ru.practicum.shareit.booking.dto.BookingRespDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserService userService;

    @Override
    @Transactional
    public BookingRespDto add(Long userId, BookingReqDto bookingReqDto) {
        User user = UserMapper.toUser(userService.findById(userId));
        Item item = itemRepository.findById(bookingReqDto.getItemId())
                .orElseThrow(() -> new NotFoundException("No item with id=" + bookingReqDto.getItemId() + " found"));
        validateBooking(bookingReqDto, user, item);
        Booking booking = BookingMapper.toBooking(user, item, bookingReqDto);
        return BookingMapper.toBookingRespDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingRespDto update(Long userId, Long bookingId, Boolean approved) {
        Booking booking = findAndValidate(userId, bookingId, 1);
        assert booking != null;
        BookingStatus status = approved ? BookingStatus.APPROVED : BookingStatus.REJECTED;
        booking.setStatus(status);
        return BookingMapper.toBookingRespDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingRespDto findByUserId(Long userId, Long bookingId) {
        Booking booking = findAndValidate(userId, bookingId, 2);
        assert booking != null;
        return BookingMapper.toBookingRespDto(booking);
    }

    @Override
    @Transactional
    public List<BookingRespDto> findAll(Long bookerId, String state) {
        userService.findById(bookerId);
        switch (validState(state)) {
            case ALL:
                return bookingRepository.findAllBookingsByBookerId(bookerId).stream()
                        .map(BookingMapper::toBookingRespDto)
                        .collect(Collectors.toList());
            case CURRENT:
                return bookingRepository.findAllCurrentBookingsByBookerId(bookerId, LocalDateTime.now()).stream()
                        .map(BookingMapper::toBookingRespDto)
                        .collect(Collectors.toList());
            case PAST:
                return bookingRepository.findAllPastBookingsByBookerId(bookerId, LocalDateTime.now()).stream()
                        .map(BookingMapper::toBookingRespDto)
                        .collect(Collectors.toList());
            case FUTURE:
                return bookingRepository.findAllFutureBookingsByBookerId(bookerId, LocalDateTime.now()).stream()
                        .map(BookingMapper::toBookingRespDto)
                        .collect(Collectors.toList());
            case WAITING:
                return bookingRepository.findAllWaitingBookingsByBookerId(bookerId, LocalDateTime.now()).stream()
                        .map(BookingMapper::toBookingRespDto)
                        .collect(Collectors.toList());
            case REJECTED:
                return bookingRepository.findAllRejectedBookingsByBookerId(bookerId, LocalDateTime.now()).stream()
                        .map(BookingMapper::toBookingRespDto)
                        .collect(Collectors.toList());
            default:
                throw new IllegalArgumentException("Unsupported state");
        }
    }

    @Override
    @Transactional
    public List<BookingRespDto> findAllByOwnerId(Long ownerId, String state) {
        userService.findById(ownerId);
        switch (validState(state)) {
            case ALL:
                return bookingRepository.findAllBookingsByOwnerId(ownerId).stream()
                        .map(BookingMapper::toBookingRespDto)
                        .collect(Collectors.toList());
            case CURRENT:
                return bookingRepository.findAllCurrentBookingsByOwnerId(ownerId, LocalDateTime.now()).stream()
                        .map(BookingMapper::toBookingRespDto)
                        .collect(Collectors.toList());
            case PAST:
                return bookingRepository.findAllPastBookingsByOwnerId(ownerId, LocalDateTime.now()).stream()
                        .map(BookingMapper::toBookingRespDto)
                        .collect(Collectors.toList());
            case FUTURE:
                return bookingRepository.findAllFutureBookingsByOwnerId(ownerId, LocalDateTime.now()).stream()
                        .map(BookingMapper::toBookingRespDto)
                        .collect(Collectors.toList());
            case WAITING:
                return bookingRepository.findAllWaitingBookingsByOwnerId(ownerId, LocalDateTime.now()).stream()
                        .map(BookingMapper::toBookingRespDto)
                        .collect(Collectors.toList());
            case REJECTED:
                return bookingRepository.findAllRejectedBookingsByOwnerId(ownerId).stream()
                        .map(BookingMapper::toBookingRespDto)
                        .collect(Collectors.toList());
            default:
                throw new IllegalArgumentException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    private void validateBooking(BookingReqDto bookingReqDto, User user, Item item) {
        if (!item.getAvailable()) {
            throw new ValidationException("Item not available for booking");
        }
        if (user.getId().equals(item.getOwner().getId())) {
            throw new NotFoundException("Item not found");
        }
        if (bookingReqDto.getStart().isAfter(bookingReqDto.getEnd()) || bookingReqDto.getStart().isEqual(bookingReqDto.getEnd())) {
            throw new ValidationException("Booking must end after start");
        }
    }

    private Booking findAndValidate(Long userId, Long bookingId, Integer caseNum) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking with id=" + bookingId + " not found"));

        switch (caseNum) {
            case 1:
                if (!booking.getItem().getOwner().getId().equals(userId)) {
                    throw new NotFoundException("User with id=" + userId + " is not the owner");
                }
                if (!booking.getStatus().equals(BookingStatus.WAITING)) {
                    throw new ValidationException("Booking has WAITING status");
                }
                return booking;
            case 2:
                if (!booking.getBooker().getId().equals(userId)
                    && !booking.getItem().getOwner().getId().equals(userId)) {
                    throw new NotFoundException("User with id=" + userId + " is not the booker or the owner");
                }
                return booking;
        }
        return null;
    }

    private BookingState validState(String bookingState) {
        BookingState state = BookingState.from(bookingState);
        if (state == null) {
            throw new IllegalArgumentException("Unknown state: " + bookingState);
        }
        return state;
    }
}

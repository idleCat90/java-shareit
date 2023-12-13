package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingReqDto;
import ru.practicum.shareit.booking.dto.BookingRespDto;

import java.util.List;

public interface BookingService {
    BookingRespDto add(Long userId, BookingReqDto bookingReqDto);

    BookingRespDto update(Long userId, Long bookingId, Boolean approved);

    BookingRespDto findByUserId(Long userId, Long bookingId);

    List<BookingRespDto> findAll(Long userId, String state, Integer from, Integer size);

    List<BookingRespDto> findAllByOwnerId(Long ownerId, String state, Integer from, Integer size);
}

package ru.practicum.shareit.booking;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.dto.BookingReqDto;
import ru.practicum.shareit.booking.dto.BookingRespDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;

@UtilityClass
public class BookingMapper {
    public Booking toBooking(User user, Item item, BookingReqDto bookingReqDto) {
        return new Booking(
                item,
                bookingReqDto.getStart(),
                bookingReqDto.getEnd(),
                user,
                BookingStatus.WAITING);
    }

    public BookingRespDto toBookingRespDto(Booking booking) {
        return new BookingRespDto(
                booking.getId(),
                ItemMapper.toItemRespDto(booking.getItem()),
                booking.getStart(),
                booking.getEnd(),
                UserMapper.toUserDto(booking.getBooker()),
                booking.getStatus());
    }

    public static BookingItemDto toBookingItemDto(Booking booking) {
        return new BookingItemDto(
                booking.getId(),
                booking.getBooker().getId());
    }
}
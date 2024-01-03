package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingReqDto;
import ru.practicum.shareit.booking.dto.BookingRespDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

import static ru.practicum.shareit.common.Constants.USER_HEADER;


@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingRespDto add(@RequestHeader(USER_HEADER) Long userId,
                              @RequestBody BookingReqDto bookingReqDto) {
        log.info("POST \"/bookings\", Body:{}, Headers:(X-Sharer-User-Id)={}", bookingReqDto, userId);
        return bookingService.add(userId, bookingReqDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingRespDto update(@RequestHeader(USER_HEADER) Long userId,
                                 @PathVariable("bookingId") Long bookingId,
                                 @RequestParam(name = "approved") Boolean approved) {
        log.info("PATCH \"/bookings/{}?approved={}\", Headers:(X-Sharer-User-Id)={}", bookingId, approved, userId);
        return bookingService.update(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingRespDto findByUserId(@RequestHeader(USER_HEADER) Long userId,
                                       @PathVariable("bookingId") Long bookingId) {
        log.info("GET \"/bookings/{}\", Headers:(X-Sharer-User-Id)={}", bookingId, userId);
        return bookingService.findByUserId(userId, bookingId);
    }

    @GetMapping
    public List<BookingRespDto> findAll(@RequestHeader(USER_HEADER) Long userId,
                                        @RequestParam(value = "state", defaultValue = "ALL") String bookingState,
                                        @RequestParam(value = "from", defaultValue = "0") Integer from,
                                        @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("GET \"/bookings?state={}\", Headers:(X-Sharer-User-Id)={}", bookingState, userId);
        return bookingService.findAll(userId, bookingState, from, size);
    }

    @GetMapping("/owner")
    public List<BookingRespDto> findAllByOwnerId(@RequestHeader(USER_HEADER) Long ownerId,
                                                 @RequestParam(value = "state", defaultValue = "ALL") String bookingState,
                                                 @RequestParam(value = "from", defaultValue = "0") Integer from,
                                                 @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("GET \"/bookings/owner?state={}\", Headers:(X-Sharer-User-Id)={}", bookingState, ownerId);
        return bookingService.findAllByOwnerId(ownerId, bookingState, from, size);
    }
}

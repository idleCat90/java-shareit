package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingRespDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentReqDto;
import ru.practicum.shareit.item.dto.CommentRespDto;
import ru.practicum.shareit.item.dto.ItemReqDto;
import ru.practicum.shareit.item.dto.ItemRespDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final UserService userService;

    @Override
    @Transactional
    public ItemRespDto add(Long userId, ItemReqDto itemReqDto) {
        UserDto userDto = userService.findById(userId);
        Item item = ItemMapper.toItem(itemReqDto);
        item.setOwner(UserMapper.toUser(userDto));
        return ItemMapper.toItemRespDto(item);
    }

    @Override
    @Transactional
    public ItemRespDto update(Long userId, Long itemId, ItemReqDto itemReqDto) {
        UserDto userDto = userService.findById(userId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("No item with id=" + itemId + " found"));
        if (!UserMapper.toUser(userDto).equals(item.getOwner())) {
            throw new NotFoundException("User with id=" + userId + " is not an owner of item with id=" + itemId);
        }
        Boolean isAvailable = itemReqDto.getAvailable();
        if (isAvailable != null) {
            item.setAvailable(isAvailable);
        }
        String description = itemReqDto.getDescription();
        if (description != null && !description.isBlank()) {
            item.setDescription(description);
        }
        String name = itemReqDto.getName();
        if (name != null && !name.isBlank()) {
            item.setName(name);
        }
        return ItemMapper.toItemRespDto(item);
    }

    @Override
    @Transactional
    public ItemRespDto findById(Long userId, Long itemId) {
        userService.findById(userId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("No item with id=" + itemId + " found"));
        ItemRespDto itemRespDto = ItemMapper.toItemRespDto(item);
        itemRespDto.setComments(getAllItemComments(itemId));
        if (!item.getOwner().getId().equals(userId)) {
            return itemRespDto;
        }
        List<Booking> bookings = bookingRepository.findAllByItemAndStatusOrderByStartAsc(item, BookingStatus.APPROVED);
        List<BookingRespDto> bookingRespDtoList = bookings.stream()
                .map(BookingMapper::toBookingRespDto)
                .collect(toList());

        itemRespDto.setLastBooking(getLastBooking(bookingRespDtoList, LocalDateTime.now()));
        itemRespDto.setNextBooking(getNextBooking(bookingRespDtoList, LocalDateTime.now()));
        return itemRespDto;
    }

    @Override
    @Transactional
    public List<ItemRespDto> findAll(Long userId) {
        UserDto owner = userService.findById(userId);
        List<Item> itemList = itemRepository.findAllByOwnerId(userId);
        List<Long> itemIdList = itemList.stream()
                .map(Item::getId)
                .collect(toList());

        Map<Long, List<CommentRespDto>> comments = commentRepository.findAllByItemIdIn(itemIdList).stream()
                .map(CommentMapper::toCommentRespDto)
                .collect(groupingBy(CommentRespDto::getItemId, toList()));

        Map<Long, List<BookingRespDto>> bookings = bookingRepository
                .findAllByItemInAndStatusOrderByStartAsc(itemList, BookingStatus.APPROVED).stream()
                .map(BookingMapper::toBookingRespDto)
                .collect(groupingBy(BookingRespDto::getItemId, toList()));

        return itemList.stream()
                .map(item -> ItemMapper.toItemRespDto(
                        item,
                        getLastBooking(bookings.get(item.getId()), LocalDateTime.now()),
                        comments.get(item.getId()),
                        getNextBooking(bookings.get(item.getId()), LocalDateTime.now())
                ))
                .collect(toList());
    }

    @Override
    @Transactional
    public List<ItemRespDto> search(Long userId, String text) {
        userService.findById(userId);
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        List<Item> itemList = itemRepository.search(text);
        return itemList.stream()
                .map(ItemMapper::toItemRespDto)
                .collect(toList());
    }

    @Override
    @Transactional
    public CommentRespDto addComment(Long userId, CommentReqDto commentReqDto, Long itemId) {
        User user = UserMapper.toUser(userService.findById(userId));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("No item with id=" + itemId + " found"));
        List<Booking> userBookings = bookingRepository.findAllByUserBookings(userId, itemId, LocalDateTime.now());

        if (userBookings.isEmpty()) {
            throw new ValidationException("User with id=" + userId + " has no bookings for item with id=" + itemId);
        }

        return CommentMapper.toCommentRespDto(commentRepository
                .save(CommentMapper.toComment(commentReqDto, item, user)));
    }

    private List<CommentRespDto> getAllItemComments(Long itemId) {
        List<Comment> comments = commentRepository.findAllByItemId(itemId);

        return comments.stream()
                .map(CommentMapper::toCommentRespDto)
                .collect(toList());
    }

    private BookingRespDto getLastBooking(List<BookingRespDto> bookings, LocalDateTime time) {
        if (bookings == null || bookings.isEmpty()) {
            return null;
        }

        return bookings
                .stream()
                .filter(bookingDTO -> !bookingDTO.getStart().isAfter(time))
                .reduce((booking1, booking2) -> booking1.getStart().isAfter(booking2.getStart()) ? booking1 : booking2)
                .orElse(null);
    }

    private BookingRespDto getNextBooking(List<BookingRespDto> bookings, LocalDateTime time) {
        if (bookings == null || bookings.isEmpty()) {
            return null;
        }

        return bookings
                .stream()
                .filter(bookingDTO -> bookingDTO.getStart().isAfter(time))
                .findFirst()
                .orElse(null);
    }
}

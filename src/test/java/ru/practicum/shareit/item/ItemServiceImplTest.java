package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentRespDto;
import ru.practicum.shareit.item.dto.ItemReqDto;
import ru.practicum.shareit.item.dto.ItemRespDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private UserService userService;
    @InjectMocks
    private ItemServiceImpl itemService;

    @Captor
    private ArgumentCaptor<Item> itemArgumentCaptor;

    private final User user = User.builder()
            .id(1L)
            .name("user")
            .email("user@mail.com")
            .build();

    private final User otherUser = User.builder()
            .id(2L)
            .name("otherUser")
            .email("otherUser@mail.com")
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
            .owner(user)
            .build();

    private final ItemRespDto itemRespDto = ItemRespDto.builder()
            .id(1L)
            .name("item")
            .description("description")
            .available(true)
            .comments(Collections.emptyList())
            .build();

    private final ItemReqDto updateItemReqDto = ItemReqDto.builder().build();

    private final Comment comment = Comment.builder()
            .id(1L)
            .text("comment")
            .created(LocalDateTime.now())
            .author(user)
            .item(item)
            .build();

    private final Booking booking = Booking.builder()
            .id(1L)
            .item(item)
            .booker(user)
            .status(BookingStatus.APPROVED)
            .start(LocalDateTime.now().minusDays(1L))
            .end(LocalDateTime.now().plusDays(1L))
            .build();

    private final Booking lastBooking = Booking.builder()
            .id(2L)
            .item(item)
            .booker(user)
            .status(BookingStatus.APPROVED)
            .start(LocalDateTime.now().minusDays(2L))
            .end(LocalDateTime.now().minusDays(1L))
            .build();

    private final Booking pastBooking = Booking.builder()
            .id(3L)
            .item(item)
            .booker(user)
            .status(BookingStatus.APPROVED)
            .start(LocalDateTime.now().minusDays(10L))
            .end(LocalDateTime.now().minusDays(9L))
            .build();

    private final Booking nextBooking = Booking.builder()
            .id(4L)
            .item(item)
            .booker(user)
            .status(BookingStatus.APPROVED)
            .start(LocalDateTime.now().plusDays(1L))
            .end(LocalDateTime.now().plusDays(2L))
            .build();

    private final Booking futureBooking = Booking.builder()
            .id(5L)
            .item(item)
            .booker(user)
            .status(BookingStatus.APPROVED)
            .start(LocalDateTime.now().plusDays(10L))
            .end(LocalDateTime.now().plusDays(20L))
            .build();

    @Test
    void addNewItem() {
        Item testItem = Item.builder()
                .name("test item")
                .description("test description")
                .available(true)
                .build();

        when(userService.findById(user.getId())).thenReturn(userDto);
        when(itemRepository.save(testItem)).thenReturn(testItem);

        ItemRespDto actualItemRespDto = itemService.add(userDto.getId(), ItemMapper.toItemReqDto(testItem));

        assertEquals("test item", actualItemRespDto.getName());
        assertEquals("test description", actualItemRespDto.getDescription());
    }

    @Test
    void findById() {
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        ItemRespDto actualItemRespDto = itemService.findById(user.getId(), item.getId());

        assertEquals(itemRespDto, actualItemRespDto);
    }

    @Test
    void update() {
        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .description("description")
                .requestor(user)
                .created(LocalDateTime.now())
                .build();

        Item updatedItem = Item.builder()
                .id(1L)
                .name("updated item")
                .description("updated description")
                .available(false)
                .owner(user)
                .request(itemRequest)
                .build();

        when(userService.findById(user.getId())).thenReturn(UserMapper.toUserDto(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(updatedItem));

        ItemRespDto savedItemDto = itemService
                .update(user.getId(), itemRespDto.getId(), ItemMapper.toItemReqDto(updatedItem));

        assertEquals("updated item", savedItemDto.getName());
        assertEquals("updated description", savedItemDto.getDescription());
    }

    @Test
    void whenUserIsNotOwner_thenUpdateThrowsNotFoundException() {
        Item updatedItem = Item.builder()
                .id(1L)
                .name("updated item")
                .description("updated description")
                .available(false)
                .owner(otherUser)
                .build();

        when(userService.findById(user.getId())).thenReturn(userDto);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(updatedItem));

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> itemService.update(user.getId(), itemRespDto.getId(), ItemMapper.toItemReqDto(updatedItem)));

        assertEquals(String.format("User with id=%s is not an owner of item with id=%s", user.getId(), item.getId()),
                notFoundException.getMessage());
    }

    @Test
    void whenItemIdIsNotValid_thenUpdateThrowsNotFoundException() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> itemService.update(user.getId(), itemRespDto.getId(), ItemMapper.toItemReqDto(item)));

        assertEquals(String.format("No item with id=%s found", item.getId()), notFoundException.getMessage());
    }

    @Test
    void findAllComments() {
        List<CommentRespDto> expectedCommentsDtoList = List.of(CommentMapper.toCommentRespDto(comment));
        when(commentRepository.findAllByItemId(item.getId())).thenReturn(List.of(comment));

        List<CommentRespDto> actualCommentDtoList = itemService.findAllItemComments(item.getId());

        assertEquals(1, actualCommentDtoList.size());
        assertEquals(expectedCommentsDtoList, actualCommentDtoList);
    }

    @Test
    void findAll() {
        when(itemRepository.findAllByOwnerId(anyLong(), any(Pageable.class))).thenReturn(List.of(item));

        List<ItemRespDto> actualItemRespDtoList = itemService.findAll(user.getId(), 0, 10);

        assertEquals(1, actualItemRespDtoList.size());
        assertEquals(1, actualItemRespDtoList.get(0).getId());
        assertEquals("item", actualItemRespDtoList.get(0).getName());
    }

    @Test
    void addComment() {
        CommentRespDto expectedCommentRespDto = CommentMapper.toCommentRespDto(comment);
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.findAllBookingsByUserAndItemId(anyLong(), anyLong(), any(LocalDateTime.class)))
                .thenReturn(List.of(booking));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentRespDto actualCommentRespDto = itemService
                .addComment(user.getId(), CommentMapper.toCommentReqDto(comment), item.getId());

        assertEquals(expectedCommentRespDto, actualCommentRespDto);
    }

    @Test
    void whenItemIdIsNotValid_thenAddCommentThrowsNotFoundException() {
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.empty());

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> itemService.addComment(user.getId(), CommentMapper.toCommentReqDto(comment), item.getId()));

        assertEquals(String.format("No item with id=%s found", item.getId()),
                notFoundException.getMessage());
    }

    @Test
    void whenUserHasNoBookings_thenAddCommentThrowsValidationException() {
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.findAllBookingsByUserAndItemId(anyLong(), anyLong(), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        ValidationException validationException = assertThrows(ValidationException.class,
                () -> itemService.addComment(user.getId(), CommentMapper.toCommentReqDto(comment), item.getId()));

        assertEquals(String.format("User with id=%s has no bookings for item with id=%s", user.getId(), item.getId()),
                validationException.getMessage());
    }
}

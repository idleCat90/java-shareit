package ru.practicum.shareit.item;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingReqDto;
import ru.practicum.shareit.booking.dto.BookingRespDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.CommentReqDto;
import ru.practicum.shareit.item.dto.CommentRespDto;
import ru.practicum.shareit.item.dto.ItemReqDto;
import ru.practicum.shareit.item.dto.ItemRespDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestReqDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemServiceIT {

    @Autowired
    private ItemService itemService;
    @Autowired
    private UserService userService;
    @Autowired
    private ItemRequestService itemRequestService;
    @Autowired
    private BookingService bookingService;

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

    private final ItemReqDto itemReqDto3 = ItemReqDto.builder()
            .name("item3")
            .description("item3 description")
            .available(true)
            .requestId(1L)
            .build();

    private final ItemRequestReqDto itemRequestReqDto = ItemRequestReqDto.builder()
            .description("request description")
            .build();

    private final BookingReqDto bookingReqDto = BookingReqDto.builder()
            .itemId(1L)
            .start(LocalDateTime.now())
            .end(LocalDateTime.now().plusSeconds(1L))
            .build();

    private final CommentReqDto commentReqDto = CommentReqDto.builder()
            .text("comment")
            .build();

    @Test
    @SneakyThrows
    void addNewComment() {
        UserDto addUserDto1 = userService.add(userDto1);
        UserDto addUserDto2 = userService.add(userDto2);
        ItemRespDto addItemRespDto = itemService.add(addUserDto2.getId(), itemReqDto2);
        BookingRespDto bookingRespDto = bookingService.add(addUserDto1.getId(), bookingReqDto);

        bookingService.update(addUserDto2.getId(), bookingRespDto.getId(), true);
        Thread.sleep(2000);
        CommentRespDto commentRespDto = itemService
                .addComment(addUserDto1.getId(), commentReqDto, addItemRespDto.getId());

        assertEquals(1L, commentRespDto.getId());
        assertEquals("comment", commentRespDto.getText());
    }

    @Test
    void addNewItem() {
        UserDto userDto = userService.add(userDto1);
        ItemRespDto itemRespDto = itemService.add(userDto.getId(), itemReqDto1);

        assertEquals(1L, itemRespDto.getId());
        assertEquals("item1", itemRespDto.getName());
    }

    @Test
    void addNewRequest() {
        UserDto userDto = userService.add(userDto1);
        itemRequestService.add(userDto.getId(), itemRequestReqDto);

        ItemRespDto itemRespDto = itemService.add(userDto.getId(), itemReqDto3);

        assertEquals(1L, itemRespDto.getRequestId());
        assertEquals("item3", itemRespDto.getName());
    }

    @Test
    void whenItemIdIsNotValid_thenGetByIdThrows() {
        Long itemId = 5L;

        assertThrows(RuntimeException.class,
                () -> itemService.findById(userDto1.getId(), itemId));
    }
}

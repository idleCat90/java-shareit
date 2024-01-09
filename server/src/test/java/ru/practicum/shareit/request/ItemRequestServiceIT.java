package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestReqDto;
import ru.practicum.shareit.request.dto.ItemRequestRespDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemRequestServiceIT {

    @Autowired
    private ItemRequestService itemRequestService;
    @Autowired
    private UserService userService;

    private final UserDto userDto = UserDto.builder()
            .name("user")
            .email("user@mail.com")
            .build();

    private final ItemRequestReqDto itemRequestReqDto = ItemRequestReqDto.builder()
            .description("description")
            .build();

    @Test
    void addNewRequest() {
        UserDto addedUserDto = userService.add(userDto);
        itemRequestService.add(addedUserDto.getId(), itemRequestReqDto);

        List<ItemRequestRespDto> actualItemRequestRespDtos = itemRequestService.findUserRequests(addedUserDto.getId());

        assertEquals("description", actualItemRequestRespDtos.get(0).getDescription());
    }

    @Test
    void whenRequestIdIsNitValid_thenFindByIdThrowsNotFoundException() {
        Long requestId = 2L;

        assertThrows(RuntimeException.class,
                () -> itemRequestService.findRequestById(userDto.getId(), requestId));
    }
}

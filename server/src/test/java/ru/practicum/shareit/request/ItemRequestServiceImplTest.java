package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestReqDto;
import ru.practicum.shareit.request.dto.ItemRequestRespDto;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceImplTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private UserService userService;
    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    private final User user = User.builder()
            .id(1L)
            .name("user")
            .email("user@mail.com")
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

    private final ItemRequest itemRequest = ItemRequest.builder()
            .id(1L)
            .description("description")
            .items(List.of(item))
            .build();

    @Test
    void addNewRequest() {
        ItemRequestReqDto itemRequestReqDto = ItemRequestMapper.toItemRequestReqDto(itemRequest);
        ItemRequestRespDto expectedItemRequestRespDto = ItemRequestMapper.toItemRequestRespDto(itemRequest);
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);

        ItemRequestRespDto actualItemRequestRespDto = itemRequestService.add(user.getId(), itemRequestReqDto);

        assertEquals(expectedItemRequestRespDto, actualItemRequestRespDto);
    }

    @Test
    void findUserRequests() {
        List<ItemRequestRespDto> expectedItemRequestRespDtos = List.of(ItemRequestMapper
                .toItemRequestRespDto(itemRequest));
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(itemRequestRepository.findAllByRequestorId(userDto.getId())).thenReturn(List.of(itemRequest));

        List<ItemRequestRespDto> actualItemRequestRespDtos = itemRequestService.findUserRequests(userDto.getId());

        assertEquals(expectedItemRequestRespDtos, actualItemRequestRespDtos);
    }

    @Test
    void findAllRequests() {
        List<ItemRequestRespDto> expectedItemRequestRespDtos = List.of(ItemRequestMapper
                .toItemRequestRespDto(itemRequest));
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(itemRequestRepository.findAllByRequestorIdNotOrderByCreatedDesc(anyLong(), any(PageRequest.class)))
                .thenReturn(List.of(itemRequest));

        List<ItemRequestRespDto> actualItemRequestRespDtos = itemRequestService
                .findAllRequests(userDto.getId(), 0, 10);

        assertEquals(expectedItemRequestRespDtos, actualItemRequestRespDtos);
    }

    @Test
    void findRequestById() {
        ItemRequestRespDto expectedItemRequestRespDto = ItemRequestMapper.toItemRequestRespDto(itemRequest);
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(itemRequestRepository.findById(itemRequest.getId())).thenReturn(Optional.of(itemRequest));

        ItemRequestRespDto actualItemRequestRespDto = itemRequestService
                .findRequestById(userDto.getId(), itemRequest.getId());

        assertEquals(expectedItemRequestRespDto, actualItemRequestRespDto);
    }

    @Test
    void whenRequestIdIsNotValid_thenFindByIdThrowsNotFoundException() {
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(itemRequestRepository.findById(itemRequest.getId())).thenReturn(Optional.empty());

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> itemRequestService.findRequestById(userDto.getId(), itemRequest.getId()));

        assertEquals(String.format("No Item Request with id=%s found", itemRequest.getId()),
                notFoundException.getMessage());
    }
}

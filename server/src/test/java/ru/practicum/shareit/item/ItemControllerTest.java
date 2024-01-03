package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.item.dto.CommentReqDto;
import ru.practicum.shareit.item.dto.CommentRespDto;
import ru.practicum.shareit.item.dto.ItemReqDto;
import ru.practicum.shareit.item.dto.ItemRespDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.common.Constants.USER_HEADER;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemService itemService;

    private final User user = User.builder()
            .id(1L)
            .name("user")
            .email("user@mail.com")
            .build();

    private final Item item = Item.builder()
            .id(1L)
            .name("item")
            .description("description")
            .owner(user)
            .build();

    @Test
    @SneakyThrows
    void whenItemIsValid_thenCreateItem() {
        Long userId = 0L;
        ItemReqDto itemReqDto = ItemReqDto.builder()
                .name("item")
                .description("description")
                .available(true)
                .build();

        when(itemService.add(userId, itemReqDto))
                .thenReturn(ItemMapper.toItemRespDto(ItemMapper.toItem(itemReqDto)));

        String result = mockMvc.perform(post("/items")
                        .header(USER_HEADER, userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemReqDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        ItemReqDto resultItemReqDto = objectMapper.readValue(result, ItemReqDto.class);

        assertEquals(itemReqDto.getName(), resultItemReqDto.getName());
        assertEquals(itemReqDto.getDescription(), resultItemReqDto.getDescription());
        assertEquals(itemReqDto.getAvailable(), resultItemReqDto.getAvailable());
    }

    @Test
    @SneakyThrows
    void whenItemIsValid_thenUpdateReturnsOk() {
        Long itemId = 0L;
        Long userId = 0L;
        ItemReqDto itemReqDto = ItemReqDto.builder()
                .name("item")
                .description("description")
                .available(true)
                .build();

        when(itemService.update(userId, itemId, itemReqDto))
                .thenReturn(ItemMapper.toItemRespDto(ItemMapper.toItem(itemReqDto)));

        String result = mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header(USER_HEADER, userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemReqDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        ItemReqDto resultItemReqDto = objectMapper.readValue(result, ItemReqDto.class);

        assertEquals(itemReqDto.getName(), resultItemReqDto.getName());
        assertEquals(itemReqDto.getDescription(), resultItemReqDto.getDescription());
        assertEquals(itemReqDto.getAvailable(), resultItemReqDto.getAvailable());
    }

    @Test
    @SneakyThrows
    void whenGet_thenReturnOk() {
        Long itemId = 0L;
        Long userId = 0L;
        ItemRespDto itemRespDto = ItemRespDto.builder()
                .id(itemId)
                .name("item")
                .description("description")
                .available(true)
                .build();

        when(itemService.findById(userId, itemId)).thenReturn(itemRespDto);

        String result = mockMvc.perform(MockMvcRequestBuilders.get("/items/{itemId}", itemId)
                        .header(USER_HEADER, userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemRespDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemRespDto), result);
    }

    @Test
    @SneakyThrows
    void whenGetAll_thenReturnOk() {
        Long userId = 0L;
        Integer from = 0;
        Integer size = 10;
        List<ItemRespDto> expectedItemRespDtoList = List.of(ItemRespDto.builder()
                .name("item")
                .description("description")
                .available(true)
                .build());

        when(itemService.findAll(userId, from, size)).thenReturn(expectedItemRespDtoList);

        String result = mockMvc.perform(MockMvcRequestBuilders.get("/items", from, size)
                        .header(USER_HEADER, userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(expectedItemRespDtoList), result);
    }

    @Test
    @SneakyThrows
    void whenSearch_thenReturnOk() {
        Long userId = 0L;
        Integer from = 0;
        Integer size = 10;
        String text = "search";
        List<ItemRespDto> expectedItemRespDtoList = List.of(ItemRespDto.builder()
                .name("item")
                .description("description")
                .available(true)
                .build());

        when(itemService.search(userId, text, from, size)).thenReturn(expectedItemRespDtoList);

        String result = mockMvc.perform(MockMvcRequestBuilders.get("/items/search", from, size)
                        .header(USER_HEADER, userId)
                        .param("text", text))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(expectedItemRespDtoList), result);
    }

    @Test
    @SneakyThrows
    void whenCommentIsValid_thenCreateReturnsOk() {
        ItemRespDto itemRespDto = itemService.add(user.getId(), ItemMapper.toItemReqDto(item));
        CommentReqDto commentReqDto = CommentReqDto.builder()
                .text("comment")
                .build();
        CommentRespDto commentRespDto = CommentRespDto.builder()
                .id(1L)
                .itemId(item.getId())
                .text(commentReqDto.getText())
                .build();

        when(itemService.addComment(user.getId(), commentReqDto, item.getId()))
                .thenReturn(commentRespDto);

        String result = mockMvc.perform(post("/items/{itemId}/comment", item.getId())
                        .header(USER_HEADER, user.getId())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(commentReqDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(commentRespDto), result);
    }
}

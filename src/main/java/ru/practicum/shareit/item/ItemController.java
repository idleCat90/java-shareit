package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentReqDto;
import ru.practicum.shareit.item.dto.CommentRespDto;
import ru.practicum.shareit.item.dto.ItemReqDto;
import ru.practicum.shareit.item.dto.ItemRespDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    public static final String USER_HEADER = "X-Sharer-User-Id";
    private final ItemService itemService;

    @PostMapping
    public ItemRespDto add(@RequestHeader(USER_HEADER) long userId,
                           @RequestBody @Valid ItemReqDto itemReqDto) {
        log.info("POST \"/item\" Body={}, Headers:(X-Sharer-User-Id)={}", itemReqDto, userId);
        return itemService.add(userId, itemReqDto);
    }

    @PatchMapping("/{itemId}")
    public ItemRespDto update(@RequestHeader(USER_HEADER) Long userId,
                              @RequestBody ItemReqDto itemReqDto,
                              @PathVariable Long itemId) {
        log.info("PATCH \"/item/{}\" Body={}, Headers:(X-Sharer-User-Id)={}", itemId, itemReqDto, userId);
        return itemService.update(userId, itemId, itemReqDto);
    }

    @GetMapping("/{itemId}")
    public ItemRespDto findById(@RequestHeader(USER_HEADER) long userId,
                                @PathVariable("itemId") Long itemId) {
        log.info("GET \"/item/{}\" , Headers:(X-Sharer-User-Id)={}", itemId, userId);
        return itemService.findById(userId, itemId);
    }

    @GetMapping
    public List<ItemRespDto> findAll(@RequestHeader(USER_HEADER) Long userId) {
        log.info("GET \"/item/\", Headers:(X-Sharer-User-Id)={}", userId);
        return itemService.findAll(userId);
    }

    @GetMapping("/search")
    public List<ItemRespDto> search(@RequestHeader(USER_HEADER) Long userId,
                                    @RequestParam(name = "text") String text) {
        log.info("GET \"/item/search?text={}\", Headers:(X-Sharer-User-Id)={}", text, userId);
        return itemService.search(userId, text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentRespDto addComment(@RequestHeader(USER_HEADER) Long userId,
                                     @Valid @RequestBody CommentReqDto commentReqDto,
                                     @PathVariable Long itemId) {
        log.info("POST \"/item/{}/comment\", Body:{}, Headers:(X-Sharer-User-Id)={}", itemId, commentReqDto, userId);
        return itemService.addComment(userId, commentReqDto, itemId);
    }
}
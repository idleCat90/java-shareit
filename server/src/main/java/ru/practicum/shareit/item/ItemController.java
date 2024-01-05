package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentReqDto;
import ru.practicum.shareit.item.dto.CommentRespDto;
import ru.practicum.shareit.item.dto.ItemReqDto;
import ru.practicum.shareit.item.dto.ItemRespDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static ru.practicum.shareit.common.Constants.USER_HEADER;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemRespDto add(@RequestHeader(USER_HEADER) Long userId,
                           @RequestBody ItemReqDto itemReqDto) {
        log.info("POST \"/item\" Body={}, Headers:(X-Sharer-User-Id)={}", itemReqDto, userId);
        return itemService.add(userId, itemReqDto);
    }

    @PatchMapping("/{itemId}")
    public ItemRespDto update(@RequestHeader(USER_HEADER) Long userId,
                              @RequestBody ItemReqDto itemReqDto,
                              @PathVariable("itemId") Long itemId) {
        log.info("PATCH \"/item/{}\" Body={}, Headers:(X-Sharer-User-Id)={}", itemId, itemReqDto, userId);
        return itemService.update(userId, itemId, itemReqDto);
    }

    @GetMapping("/{itemId}")
    public ItemRespDto findById(@RequestHeader(USER_HEADER) Long userId,
                                @PathVariable("itemId") Long itemId) {
        log.info("GET \"/item/{}\" , Headers:(X-Sharer-User-Id)={}", itemId, userId);
        return itemService.findById(userId, itemId);
    }

    @GetMapping
    public List<ItemRespDto> findAll(@RequestHeader(USER_HEADER) Long userId,
                                     @RequestParam(value = "from", defaultValue = "0") Integer from,
                                     @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("GET \"/item/\", Headers:(X-Sharer-User-Id)={}", userId);
        return itemService.findAll(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemRespDto> search(@RequestHeader(USER_HEADER) Long userId,
                                    @RequestParam(name = "text") String text,
                                    @RequestParam(value = "from", defaultValue = "0") Integer from,
                                    @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("GET \"/item/search?text={}\", Headers:(X-Sharer-User-Id)={}", text, userId);
        return itemService.search(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentRespDto addComment(@RequestHeader(USER_HEADER) Long userId,
                                     @RequestBody CommentReqDto commentReqDto,
                                     @PathVariable Long itemId) {
        log.info("POST \"/item/{}/comment\", Body:{}, Headers:(X-Sharer-User-Id)={}", itemId, commentReqDto, userId);
        return itemService.addComment(userId, commentReqDto, itemId);
    }
}
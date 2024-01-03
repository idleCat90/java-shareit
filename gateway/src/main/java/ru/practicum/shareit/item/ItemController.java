package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import static ru.practicum.shareit.common.Constants.USER_HEADER;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient client;

    @PostMapping
    public ResponseEntity<Object> add(@RequestHeader(USER_HEADER) Long userId,
                                      @Valid @RequestBody ItemDto itemDto) {
        log.info("POST \"/item\" Body={}, Headers:(X-Sharer-User-Id)={}", itemDto, userId);
        return client.add(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestHeader(USER_HEADER) Long userId,
                                         @RequestBody ItemDto itemDto,
                                         @PathVariable("itemId") Long itemId) {
        log.info("PATCH \"/item/{}\" Body={}, Headers:(X-Sharer-User-Id)={}", itemId, itemDto, userId);
        return client.update(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> get(@RequestHeader(USER_HEADER) Long userId,
                                      @PathVariable("itemId") Long itemId) {
        log.info("GET \"/item/{}\" , Headers:(X-Sharer-User-Id)={}", itemId, userId);
        return client.get(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getAll(@RequestHeader(USER_HEADER) Long userId,
                                         @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
                                         @RequestParam(value = "size", defaultValue = "10") @Min(1) Integer size) {
        log.info("GET \"/item/\", Headers:(X-Sharer-User-Id)={}", userId);
        return client.getAll(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestHeader(USER_HEADER) Long userId,
                                         @RequestParam(name = "text") String text,
                                         @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
                                         @RequestParam(value = "size", defaultValue = "10") @Min(1) Integer size) {
        log.info("GET \"/item/search?text={}\", Headers:(X-Sharer-User-Id)={}", text, userId);
        return client.search(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(USER_HEADER) Long userId,
                                             @Valid @RequestBody CommentDto commentDto,
                                             @PathVariable Long itemId) {
        log.info("POST \"/item/{}/comment\", Body:{}, Headers:(X-Sharer-User-Id)={}", itemId, commentDto, userId);
        return client.addComment(userId, commentDto, itemId);
    }
}
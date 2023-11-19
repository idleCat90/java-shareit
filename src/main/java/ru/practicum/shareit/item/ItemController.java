package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.marker.OnCreate;
import ru.practicum.shareit.marker.OnUpdate;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/items")
public class ItemController {
    ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto create(@RequestBody @Validated(OnCreate.class) ItemDto itemDto,
                          @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("POST \"/item\" Body={}, Headers:(X-Sharer-User-Id)={}", itemDto, userId);
        ItemDto itemToReturn = itemService.create(itemDto, userId);
        log.debug(itemToReturn.toString());
        return itemToReturn;
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestBody @Validated(OnUpdate.class) ItemDto itemDto,
                          @PathVariable long itemId,
                          @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("PUT \"/item/" + itemId + "\" Body={}, Headers:(X-Sharer-User-Id)={}", itemDto, userId);
        itemDto.setId(itemId);
        ItemDto itemToReturn = itemService.update(itemDto, userId);
        log.debug(itemToReturn.toString());
        return itemToReturn;
    }

    @GetMapping
    public List<ItemDto> getByUserId(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("GET \"/item\" , Headers:(X-Sharer-User-Id)={}", userId);
        List<ItemDto> listToReturn = itemService.getByOwnerId(userId);
        log.debug(listToReturn.toString());
        return listToReturn;
    }

    @GetMapping("/{itemId}")
    public ItemDto getByItemId(@PathVariable long itemId,
                               @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("GET \"/item/" + itemId + "\" , Headers:(X-Sharer-User-Id)={}", userId);
        ItemDto itemReturn = itemService.getByItemId(itemId);
        log.debug(itemReturn.toString());
        return itemReturn;
    }

    @GetMapping("/search")
    public List<ItemDto> getViaSubstringSearch(@RequestParam String text,
                                               @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("GET \"/item/search?text=" + text + "\" , Headers:(X-Sharer-User-Id)={}", userId);
        List<ItemDto> itemReturn = itemService.search(text);
        log.debug(itemReturn.toString());
        return itemReturn;
    }

    @DeleteMapping("/{itemId}")
    public void deleteByUserIdAndItemId(@PathVariable long itemId,
                                        @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("DELETE \"/item/{}\" , Headers:(X-Sharer-User-Id)={}", itemId, userId);
        itemService.deleteByItemId(itemId, userId);
    }
}
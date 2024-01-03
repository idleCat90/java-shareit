package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestReqDto;
import ru.practicum.shareit.request.dto.ItemRequestRespDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import java.util.List;

import static ru.practicum.shareit.item.ItemController.USER_HEADER;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestRespDto add(@RequestHeader(USER_HEADER) Long userId,
                                  @Valid @RequestBody ItemRequestReqDto reqDto) {
        return itemRequestService.add(userId, reqDto);
    }

    @GetMapping
    public List<ItemRequestRespDto> findUserRequests(@RequestHeader(USER_HEADER) Long userId) {
        return itemRequestService.findUserRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestRespDto> findAllRequests(@RequestHeader(USER_HEADER) Long userId,
                                                    @RequestParam(name = "from", defaultValue = "0")
                                                    @Min(0) Integer from,
                                                    @RequestParam(name = "size", defaultValue = "10")
                                                    @Min(1) Integer size) {
        return itemRequestService.findAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestRespDto findRequestById(@RequestHeader(USER_HEADER) Long userId,
                                              @PathVariable Long requestId) {
        return itemRequestService.findRequestById(userId, requestId);
    }
}

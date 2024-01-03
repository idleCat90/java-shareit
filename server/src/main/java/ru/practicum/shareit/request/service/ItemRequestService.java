package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestReqDto;
import ru.practicum.shareit.request.dto.ItemRequestRespDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestRespDto add(Long userId, ItemRequestReqDto reqDto);

    List<ItemRequestRespDto> findUserRequests(Long userId);

    List<ItemRequestRespDto> findAllRequests(Long userId, Integer from, Integer size);

    ItemRequestRespDto findRequestById(Long userId, Long requestId);
}

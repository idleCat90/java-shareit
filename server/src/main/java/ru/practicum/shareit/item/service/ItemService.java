package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentReqDto;
import ru.practicum.shareit.item.dto.CommentRespDto;
import ru.practicum.shareit.item.dto.ItemReqDto;
import ru.practicum.shareit.item.dto.ItemRespDto;

import java.util.List;

public interface ItemService {
    ItemRespDto add(Long userId, ItemReqDto itemReqDto);

    ItemRespDto update(Long userId, Long itemId, ItemReqDto itemReqDto);

    ItemRespDto findById(Long userId, Long itemId);

    List<ItemRespDto> findAll(Long userId, Integer from, Integer size);

    List<ItemRespDto> search(Long userId, String text, Integer from, Integer size);

    CommentRespDto addComment(Long userId, CommentReqDto commentReqDto, Long itemId);
}

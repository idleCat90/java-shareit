package ru.practicum.shareit.request;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemRespDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestReqDto;
import ru.practicum.shareit.request.dto.ItemRequestRespDto;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@UtilityClass
public class ItemRequestMapper {

    public ItemRequest toItemRequest(User user, ItemRequestReqDto reqDto) {
        return ItemRequest.builder()
                .description(reqDto.getDescription())
                .build();
    }

    public ItemRequestReqDto toItemRequestReqDto(ItemRequest itemRequest) {
        return ItemRequestReqDto.builder()
                .description(itemRequest.getDescription())
                .build();
    }

    public ItemRequestRespDto toItemRequestRespDto(ItemRequest itemRequest) {
        List<ItemRespDto> itemRespDtoList = new ArrayList<>();

        if (!Objects.isNull(itemRequest.getItems())) {
            itemRespDtoList = itemRequest.getItems().stream()
                    .map(ItemMapper::toItemRespDto)
                    .collect(Collectors.toList());
        }

        return ItemRequestRespDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(itemRespDtoList)
                .build();
    }
}

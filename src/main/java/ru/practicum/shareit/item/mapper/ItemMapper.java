package ru.practicum.shareit.item.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingRespDto;
import ru.practicum.shareit.item.dto.CommentRespDto;
import ru.practicum.shareit.item.dto.ItemReqDto;
import ru.practicum.shareit.item.dto.ItemRespDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@UtilityClass
public class ItemMapper {
    public ItemReqDto toItemReqDto(Item item) {
        ItemReqDto itemReqDto = new ItemReqDto(
                item.getName(),
                item.getDescription(),
                item.getAvailable());
        if (item.getRequest() != null) {
            itemReqDto.setRequestId(item.getRequest().getId());
        }
        return itemReqDto;
    }

    public ItemRespDto toItemRespDto(Item item) {
        ItemRespDto itemRespDto = new ItemRespDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable());

        if (item.getRequest() != null) {
            itemRespDto.setRequestId(item.getRequest().getId());
        }
        return itemRespDto;
    }

    public ItemRespDto toItemRespDto(Item item,
                                     BookingRespDto lastBooking,
                                     List<CommentRespDto> comments,
                                     BookingRespDto nextBooking) {
        return new ItemRespDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                lastBooking,
                comments,
                nextBooking
        );
    }

    public Item toItem(ItemReqDto itemReqDto) {
        return new Item(
                itemReqDto.getName(),
                itemReqDto.getDescription(),
                itemReqDto.getAvailable());
    }
}

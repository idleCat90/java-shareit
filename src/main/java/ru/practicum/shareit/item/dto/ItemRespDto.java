package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.booking.dto.BookingRespDto;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemRespDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingRespDto lastBooking;
    private List<CommentRespDto> comments;
    private BookingRespDto nextBooking;

    public ItemRespDto(Long id, String name, String description, Boolean available) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
    }
}
package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.booking.dto.BookingRespDto;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemRespDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingRespDto lastBooking;
    private List<CommentRespDto> comments;
    private BookingRespDto nextBooking;
    private Long requestId;

    public ItemRespDto(Long id, String name, String description, Boolean available) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
    }

    public ItemRespDto(Long id, String name, String description, Boolean available, BookingRespDto lastBooking, List<CommentRespDto> comments, BookingRespDto nextBooking) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.lastBooking = lastBooking;
        this.comments = comments;
        this.nextBooking = nextBooking;
    }
}
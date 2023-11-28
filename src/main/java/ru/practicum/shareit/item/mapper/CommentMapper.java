package ru.practicum.shareit.item.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.CommentReqDto;
import ru.practicum.shareit.item.dto.CommentRespDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

@UtilityClass
public class CommentMapper {
    public CommentReqDto toCommentDto(Comment comment) {
        return new CommentReqDto(comment.getText());
    }

    public CommentRespDto toCommentRespDto(Comment comment) {
        return new CommentRespDto(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated(),
                comment.getItem().getId());
    }

    public Comment toComment(CommentReqDto commentReqDto, Item item, User user) {
        return new Comment(
                commentReqDto.getText(),
                item,
                user);
    }
}
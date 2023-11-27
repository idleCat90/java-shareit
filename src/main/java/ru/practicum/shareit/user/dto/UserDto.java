package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.marker.*;
import ru.practicum.shareit.validation.NotEmptyIfNotNull;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
public class UserDto {
    private Long id;
    @NotBlank(groups = OnCreate.class, message = "Invalid user name, should not be null or empty")
    @NotEmptyIfNotNull(groups = OnUpdate.class)
    private String name;
    @Email(groups = {OnCreate.class, OnUpdate.class}, message = "Invalid user email")
    @NotBlank(groups = OnCreate.class, message = "User email should not be null or empty")
    private String email;


}

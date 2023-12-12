package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.user.marker.OnCreate;
import ru.practicum.shareit.user.marker.OnUpdate;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class UserDto {
    private Long id;
    @NotBlank(groups = OnCreate.class)
    private String name;
    @NotBlank(groups = OnCreate.class)
    @Email(groups = {OnCreate.class, OnUpdate.class})
    private String email;
}

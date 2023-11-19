package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
public class UserDaoImpl implements UserDao {
    HashMap<Long, User> usersById;
    HashMap<String, User> usersByEmail;
    Long idCounter = 0L;

    public UserDaoImpl() {
        this.usersById = new HashMap<>();
        this.usersByEmail = new HashMap<>();
    }

    @Override
    public User create(User user) {
        Long userId = getNewId();
        user.setId(userId);
        usersById.put(userId, user);
        usersByEmail.put(user.getEmail(), user);
        return user;
    }

    @Override
    public Optional<User> getById(Long id) {
        User user = usersById.get(id);
        if (user == null) {
            return Optional.empty();
        } else {
            return Optional.of(
                    User.builder()
                            .id(user.getId())
                            .name(user.getName())
                            .email(user.getEmail())
                            .build());
        }
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(usersById.values());
    }

    @Override
    public void update(User user) {
        long id = user.getId();
        String oldEmail = usersById.get(id)
                .getEmail();

        usersByEmail.remove(oldEmail);
        usersByEmail.put(user.getEmail(), user);
        usersById.put(id, user);
    }

    @Override
    public void deleteById(Long id) {
        String oldEmail = usersById.get(id).getEmail();
        usersById.remove(id);
        usersByEmail.remove(oldEmail);
    }

    @Override
    public boolean isEmailUnique(UserDto userDto) {
        User user = usersByEmail.get(userDto.getEmail());
        return user == null || Objects.equals(user.getId(), userDto.getId());
    }

    private Long getNewId() {
        return ++idCounter;
    }
}

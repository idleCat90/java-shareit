package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemDao {
    Item create(Item item);

    void update(Item item);

    List<Item> getByOwnerId(Long userId);

    Optional<Item> getByItemId(Long userId);

    List<Item> search(String text);

    void deleteByItemIdAndOwnerId(long itemId, long ownerId);

    boolean isOwnerOfItem(long ownerId, long itemId);
}

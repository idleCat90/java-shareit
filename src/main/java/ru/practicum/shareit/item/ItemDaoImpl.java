package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class ItemDaoImpl implements ItemDao {
    private HashMap<Long, Item> idItems;
    private HashMap<Long, List<Item>> ownerIdItems;

    long idCounter = 0L;

    public ItemDaoImpl() {
        idItems = new HashMap<>();
        ownerIdItems = new HashMap<>();
    }

    private long getNewId() {
        return ++idCounter;
    }

    @Override
    public Item create(Item item) {
        long itemId = getNewId();
        long ownerId = item.getOwner().getId();
        item.setId(itemId);
        List<Item> ownerItems = ownerIdItems.getOrDefault(ownerId, new ArrayList<>());

        ownerItems.add(item);
        ownerIdItems.put(ownerId, ownerItems);

        idItems.put(itemId, item);
        return item;
    }

    @Override
    public void update(Item item) {
        long ownerId = item.getOwner().getId();
        List<Item> ownerItems = ownerIdItems.getOrDefault(ownerId, new ArrayList<>());

        ownerItems.remove(item);
        ownerItems.add(item);
        ownerIdItems.put(ownerId, ownerItems);

        idItems.put(item.getId(), item);
    }

    @Override
    public List<Item> getByOwnerId(Long ownerId) {
        return ownerIdItems.getOrDefault(ownerId, new ArrayList<>());
    }

    @Override
    public Optional<Item> getByItemId(Long itemId) {
        return Optional.ofNullable(
                idItems.get(itemId));
    }

    @Override
    public List<Item> search(String text) {
        return idItems.values().stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase()) ||
                        item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteByItemIdAndOwnerId(long itemId, long ownerId) {
        List<Item> itemList = ownerIdItems.get(ownerId);
        Item itemToDelete = Item.builder()
                .id(itemId)
                .build();

        idItems.remove(itemId);
        itemList.remove(itemToDelete);
    }

    @Override
    public boolean isOwnerOfItem(long ownerId, long itemId) {
        List<Item> ownerItems = ownerIdItems.getOrDefault(ownerId, new ArrayList<>());
        Item item = idItems.get(itemId);
        return ownerItems.contains(item);
    }
}

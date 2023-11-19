package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserDao;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {
    ItemDao itemDao;
    UserDao userDao;
    ItemMapper itemMapper;

    @Autowired
    public ItemServiceImpl(ItemDao itemDao, UserDao userDao, ItemMapper itemMapper) {
        this.itemDao = itemDao;
        this.userDao = userDao;
        this.itemMapper = itemMapper;
    }

    @Override
    public ItemDto create(ItemDto itemDto, long ownerId) {
        User owner = userDao.getById(ownerId)
                .orElseThrow(() -> new NotFoundException("User not found by Id " + ownerId));

        Item itemToCreate = itemMapper.toItem(itemDto, owner);
        Item returnedItem = itemDao.create(itemToCreate);
        itemDto.setId(returnedItem.getId());
        return itemDto;
    }

    @Override
    public ItemDto update(ItemDto itemDto, long ownerId) {
        long itemId = itemDto.getId();

        Item item = itemDao.getByItemId(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found by id: " + itemId));
        // Не понимаю почему проверку "владелец ли переданный пользователь данного объекта" нужно перенести в Dao,
        // а проверка "есть ли вообще данный пользователь в базе" остается в сервисе.
        if (!itemDao.isOwnerOfItem(ownerId, itemId)) {
            throw new NotFoundException("User with id " + ownerId + " doesn't have item with id " + itemId);
        }

        itemMapper.updateItemByDto(itemDto, item);
        itemDao.update(item);
        return itemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getByOwnerId(long userId) {
        List<Item> itemList = itemDao.getByOwnerId(userId);
        return itemMapper.toItemDtoList(itemList);
    }

    @Override
    public ItemDto getByItemId(long itemId) {
        Item item = itemDao.getByItemId(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found by Id " + itemId));
        return itemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> search(String text) {
        List<Item> itemList = itemDao.search(text.toLowerCase());
        return itemMapper.toItemDtoList(itemList);
    }

    @Override
    public void deleteByItemId(long itemId, long ownerId) {
        itemDao.deleteByItemIdAndOwnerId(itemId, ownerId);
    }
}

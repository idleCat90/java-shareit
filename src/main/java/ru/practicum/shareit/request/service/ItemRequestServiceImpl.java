package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestReqDto;
import ru.practicum.shareit.request.dto.ItemRequestRespDto;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final UserService userService;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    @Transactional
    public ItemRequestRespDto add(Long userId, ItemRequestReqDto reqDto) {
        User user = UserMapper.toUser(userService.findById(userId));
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(user, reqDto);
        itemRequest.setRequestor(user);
        return ItemRequestMapper.toItemRequestRespDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestRespDto> findUserRequests(Long userId) {
        UserMapper.toUser(userService.findById(userId));
        List<ItemRequest> itemRequestList = itemRequestRepository.findAllByRequestorId(userId);
        return itemRequestList.stream()
                .map(ItemRequestMapper::toItemRequestRespDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestRespDto> findAllRequests(Long userId, Integer from, Integer size) {
        UserMapper.toUser(userService.findById(userId));
        List<ItemRequest> itemRequestList = itemRequestRepository
                .findAllByRequestorIdNotOrderByCreatedDesc(userId, PageRequest.of(from / size, size));
        return itemRequestList.stream()
                .map(ItemRequestMapper::toItemRequestRespDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestRespDto findRequestById(Long userId, Long requestId) {
        userService.findById(userId);
        Optional<ItemRequest> request = itemRequestRepository.findById(requestId);
        if (request.isEmpty()) {
            throw new NotFoundException("No Item Request with id=" + requestId + " found");
        }
        return ItemRequestMapper.toItemRequestRespDto(request.get());
    }
}
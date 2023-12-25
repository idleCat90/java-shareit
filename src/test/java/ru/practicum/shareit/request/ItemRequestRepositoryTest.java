package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ItemRequestRepositoryTest {

    @Autowired
    private ItemRequestRepository itemRequestRepository;
    @Autowired
    private TestEntityManager testEntityManager;

    private final User user1 = User.builder()
            .name("user1")
            .email("user1@mail.com")
            .build();

    private final User user2 = User.builder()
            .name("user2")
            .email("user2@mail.com")
            .build();

    private final Item item = Item.builder()
            .name("item")
            .description("description")
            .available(true)
            .owner(user1)
            .build();

    private final ItemRequest itemRequest1 = ItemRequest.builder()
            .items(List.of(item))
            .description("request1")
            .created(LocalDateTime.now())
            .requestor(user1)
            .build();

    private final ItemRequest itemRequest2 = ItemRequest.builder()
            .items(List.of(item))
            .description("request2")
            .created(LocalDateTime.now())
            .requestor(user2)
            .build();

    @BeforeEach
    void init() {
        testEntityManager.persist(user1);
        testEntityManager.persist(user2);
        testEntityManager.persist(item);
        testEntityManager.flush();
        itemRequestRepository.save(itemRequest1);
        itemRequestRepository.save(itemRequest2);
    }

    @Test
    void findAllByRequestorId() {
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestorId(1L);

        assertEquals(itemRequests.size(), 1);
        assertEquals(itemRequests.get(0).getDescription(), "request1");
    }
}

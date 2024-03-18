package com.vp.alf.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vp.alf.common.configuration.database.AlfEventServiceTestBase;
import com.vp.alf.common.configuration.extensions.AlfMariaDBTestExtension;
import com.vp.alf.common.external_services.AlfUserService;
import com.vp.alf.component.event.model.dao.AlfEventDao;
import com.vp.alf.component.event.repository.AlfEventRepository;
import com.vp.alf.component.event.service.AlfEventService;
import event.AlfEventContext;
import event.AlfEventDto;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.test.StepVerifier;

import java.util.Calendar;
import java.util.List;

import static java.util.Calendar.MONTH;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;

@SpringBootTest
@ExtendWith({AlfMariaDBTestExtension.class})
public class AlfEventServiceTest extends AlfEventServiceTestBase {

    @Autowired
    AlfEventService eventService;

    @Autowired
    AlfEventRepository eventRepository;

    @MockBean
    AlfUserService userService;

    @Autowired
    ObjectMapper mapper;

    @Test
    void shouldCreateEvent() {
        AlfEventDto dto1 = createEventDto();
        AlfEventDto dto2 = createEventDto();
        dto2.setStartAt(null);
        StepVerifier.create(eventService.saveEvent(List.of(dto1, dto2)))
                .consumeNextWith(response -> {
                    assertEquals(1, response.getNotUpsert());
                    assertEquals(1, response.getUpsert());
                    AlfEventDto notValidEvent = response.getNotValidEvents().stream().findFirst().orElse(null);
                    assert notValidEvent != null;
                    assertEquals("missing event start date", notValidEvent.getCause());
                })
                .verifyComplete();

        StepVerifier.create(eventRepository.findAll())
                .expectNextCount(1)
                .verifyComplete();

        Mockito.verify(userService, times(1)).createEvent(anyLong());
    }

    @Test
    void shouldUpdateEvent() {
        AlfEventDto dto1 = createEventDto();
        eventService.saveEvent(List.of(dto1)).block();

        AlfEventDao eventDao = eventRepository.findAll().take(1).single().block();
        assert eventDao != null;
        eventDao.setEventName("New event name");

        AlfEventDto dto = mapper.convertValue(eventDao, AlfEventDto.class);

        StepVerifier.create(eventService.updateEvent(List.of(dto)))
                .expectNextMatches(e -> 1 == e.getUpsert())
                .verifyComplete();

        StepVerifier.create(eventRepository.findById(dto.getId()))
                .expectNextMatches(e -> e.getEventName().equals("New event name"))
                .verifyComplete();

    }

    @Test
    void shouldDeleteEvent() {
        AlfEventDto dto1 = createEventDto();
        eventService.saveEvent(List.of(dto1)).block();

        AlfEventDao eventDao = eventRepository.findAll().take(1).single().block();

        assert eventDao != null;
        StepVerifier.create(eventService.deleteEvents(List.of(eventDao.getId())))
                .verifyComplete();

        StepVerifier.create(eventRepository.findById(eventDao.getId()))
                .verifyComplete();

        Mockito.verify(userService, times(1)).deleteEvents(anyList());
    }

    @Test
    void shouldAddParticipant() {
        AlfEventDto dto1 = createEventDto();
        eventService.saveEvent(List.of(dto1)).block();

        AlfEventDao eventDao = eventRepository.findAll().take(1).single().block();

        assert eventDao != null;
        StepVerifier.create(eventService.addParticipant(eventDao.getId()))
                .expectNext(Boolean.TRUE)
                .verifyComplete();

        StepVerifier.create(eventRepository.findById(eventDao.getId()))
                .expectNextMatches(e -> 1 == e.getParticipants())
                .verifyComplete();
    }

    @Test
    void shouldFindByContext() {
        AlfEventDto dto1 = createEventDto();
        dto1.setLocation("New location");
        eventService.saveEvent(List.of(dto1)).block();

        AlfEventDao eventDao = eventRepository.findAll().take(1).single().block();

        assert eventDao != null;
        StepVerifier.create(eventService.getEventsByContext(AlfEventContext.builder()
                        .location("New location")
                        .build()))
                .expectNext(dto1)
                .verifyComplete();

        StepVerifier.create(eventRepository.findById(eventDao.getId()))
                .expectNextMatches(e -> 1 == e.getParticipants())
                .verifyComplete();
    }

    private AlfEventDto createEventDto() {
        Calendar instance = Calendar.getInstance();
        instance.add(MONTH, 2);

        return AlfEventDto.builder()
                .startAt(instance.getTime())
                .eventName(RandomStringUtils.random(6, true, false))
                .location(RandomStringUtils.random(6, true, false))
                .venue(RandomStringUtils.random(6, true, false))
                .build();
    }
}

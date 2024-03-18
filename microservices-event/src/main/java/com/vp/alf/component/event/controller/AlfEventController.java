package com.vp.alf.component.event.controller;


import com.vp.alf.component.event.service.AlfEventService;
import event.AlfEventContext;
import event.AlfEventDto;
import event.AlfEventUpsertResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;

@RequiredArgsConstructor
@Controller
public class AlfEventController {

    private final AlfEventService eventService;

    @MessageMapping("event.create")
    public Mono<AlfEventUpsertResponse> createEvents(Collection<AlfEventDto> events) {
        return eventService.saveEvent(events);
    }

    @MessageMapping("event.update")
    public Mono<AlfEventUpsertResponse> updateEvents(Collection<AlfEventDto> events) {
        return eventService.updateEvent(events);
    }

    @MessageMapping("event.delete")
    public Mono<Void> deleteEvents(Collection<Long> eventIds) {
        return eventService.deleteEvents(eventIds);
    }

    @MessageMapping("event.byContext")
    public Flux<AlfEventDto> getEvents(AlfEventContext context) {
        return eventService.getEventsByContext(context);
    }

    @MessageMapping("event.addParticipant.{eventId}")
    public Mono<Boolean> addParticipant(@DestinationVariable Long eventId) {
        return eventService.addParticipant(eventId);
    }

    @MessageMapping("event.findAll")
    public Flux<AlfEventDto> findAll() {
        return eventService.findAll();
    }

}

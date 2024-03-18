package com.vp.alf.component.event.controller;

import com.vp.alf.component.event.service.AlfEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

import java.util.Collection;

@Controller
@RequiredArgsConstructor
public class AlfEventController {

    private final AlfEventService eventService;

    @MessageMapping("user.event.create")
    public Mono<Void> createEvent(Long eventId) {
        return eventService.createEvent(eventId);
    }

    @MessageMapping("user.delete.event")
    public Mono<Void> deleteEvents(Collection<Long> eventIds) {
        return eventService.deleteEvents(eventIds);
    }
}

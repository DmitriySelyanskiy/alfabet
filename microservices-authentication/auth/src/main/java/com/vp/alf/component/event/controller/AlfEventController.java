package com.vp.alf.component.event.controller;


import com.vp.alf.component.event.service.AlfEventService;
import event.AlfEventContext;
import event.AlfEventDto;
import event.AlfEventUpsertResponse;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/event")
public class AlfEventController {

    private final AlfEventService eventService;

    @PostMapping("/create")
    @RateLimiter(name = "alf")
    public Mono<AlfEventUpsertResponse> createEvents(@RequestBody Collection<AlfEventDto> events) {
        return eventService.saveEvent(events);
    }

    @PutMapping("/update")
    public Mono<AlfEventUpsertResponse> updateEvents(@RequestBody Collection<AlfEventDto> events) {
        return eventService.updateEvent(events);
    }

    @DeleteMapping("/delete")
    public Mono<Void> deleteEvents(@RequestBody Collection<Long> eventIds) {
        return eventService.deleteEvents(eventIds);
    }

    @PostMapping("/byContext")
    @RateLimiter(name = "alf")
    public Flux<AlfEventDto> getEvents(@RequestBody AlfEventContext context) {
        return eventService.getEventsByContext(context);
    }

    @PostMapping("/all")
    @RateLimiter(name = "alf")
    public Flux<AlfEventDto> findAll() {
        return eventService.findAll();
    }
}

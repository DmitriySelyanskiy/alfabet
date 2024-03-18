package com.vp.alf.component.event.service;

import com.vp.alf.common.controls.AlfNeo4jOperations;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlfEventService {

    private final AlfNeo4jOperations operations;

    public Mono<Void> createEvent(Long eventId) {
        return operations.createEvent(eventId)
                .switchIfEmpty(Mono.defer(
                        () -> {
                            log.error("Event with id {} was not created", eventId);
                            return Mono.error(new RuntimeException());
                        }
                ))
                .then();
    }

    public Mono<Void> deleteEvents(Collection<Long> eventIds) {
        return operations.deleteEvents(eventIds)
                .then();
    }
}

package com.vp.alf.component.event.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.vp.alf.common.external_services.AlfUserService;
import com.vp.alf.component.event.model.dao.AlfEventDao;
import com.vp.alf.component.event.model.dto.AlfNotificationDto;
import com.vp.alf.component.event.repository.AlfEventRepository;
import com.vp.alf.component.event.scheduler.AlfEventScheduler;
import event.AlfEventContext;
import event.AlfEventDto;
import event.AlfEventUpsertResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class AlfEventService {

    private final AlfEventRepository eventRepository;
    private final ObjectMapper mapper;
    private final AlfEventConstraint constraint;
    private final AlfEventScheduler scheduler;
    private final AlfUserService userService;
    @Qualifier("event-notification")
    private final Sinks.Many<AlfNotificationDto> sinkRequester;

    public Mono<AlfEventUpsertResponse> saveEvent(Collection<AlfEventDto> events) {
        Set<AlfEventDto> notValidEvents = Sets.newHashSet();
        Function<AlfEventDto, AlfEventDto> function = e -> {
            e.setCreatedAt(Date.from(Instant.now()));
            return e;
        };

        return upsetEvent(events, function, notValidEvents, false)
                .flatMap(event -> scheduler.scheduleNotifyEvent(event)
                        .flatMap(userService::createEvent)
                        .onErrorResume(e -> {
                            AlfEventDto eventDto = mapper.convertValue(event, AlfEventDto.class);
                            eventDto.setCause(e.getLocalizedMessage());
                            notValidEvents.add(eventDto);
                            return eventRepository.delete(event);
                        })
                )
                .then(
                        Mono.defer(
                                () -> Mono.just(AlfEventUpsertResponse.builder()
                                        .upsert(events.size() - notValidEvents.size())
                                        .notUpsert(notValidEvents.size())
                                        .notValidEvents(notValidEvents)
                                        .build())
                        )
                );
    }

    public Mono<AlfEventUpsertResponse> updateEvent(Collection<AlfEventDto> events) {
        Set<AlfEventDto> notValidEvents = Sets.newHashSet();
        List<Long> eventIds = CollectionUtils.collect(events, AlfEventDto::getId, Lists.newArrayList());
        Map<Long, AlfEventDao> existingEvents = Maps.newHashMap();

        return eventRepository.findAllById(eventIds)
                .reduce(existingEvents, (map, e) -> {
                    map.put(e.getId(), e);
                    return map;
                })
                .thenMany(Flux.defer(() -> upsetEvent(events, Function.identity(), notValidEvents, true)))
                .flatMap(event ->
                        Flux.concat(
                                        scheduler.rescheduleJobIfNeeded(mapper.convertValue(event, AlfEventDto.class), notValidEvents),
                                        notifyOnChanges(event)
                                )
                                .doOnError(e -> {
                                    log.error("Couldn't make changes for event {}", event.getId());
                                    AlfEventDto eventDto = mapper.convertValue(event, AlfEventDto.class);
                                    notValidEvents.add(eventDto);
                                })
                )
                .thenMany(Flux.defer(() -> rollBack(existingEvents, notValidEvents)))
                .then(
                        Mono.just(AlfEventUpsertResponse.builder()
                                .upsert(events.size() - notValidEvents.size())
                                .notUpsert(notValidEvents.size())
                                .notValidEvents(notValidEvents)
                                .build())

                );
    }

    private Flux<AlfEventDao> rollBack(Map<Long, AlfEventDao> existingEvents, Set<AlfEventDto> notValidEvents) {
        return notValidEvents.stream()
                .map(AlfEventDto::getId)
                .map(existingEvents::get)
                .collect(
                        Collectors
                                .collectingAndThen(
                                        Collectors.toList(),
                                        eventRepository::saveAll
                                ));
    }

    private Mono<Void> notifyOnChanges(AlfEventDao event) {
        sinkRequester.tryEmitNext(AlfNotificationDto.builder()
                .eventId(String.valueOf(event.getId()))
                .message(String.format("The Event %s has been changed", event.getEventName()))
                .build());
        return Mono.empty();
    }

    private Flux<AlfEventDao> upsetEvent(Collection<AlfEventDto> events,
                                         Function<AlfEventDto, AlfEventDto> function,
                                         Set<AlfEventDto> notValidEvents,
                                         boolean isUpdate) {
        return events.stream()
                .filter(eventDto -> constraint.validateBeforeUpsert(eventDto, notValidEvents, isUpdate))
                .map(function)
                .map(e -> mapper.convertValue(e, AlfEventDao.class))
                .collect(
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                eventRepository::saveAll
                        ));
    }

    public Mono<Void> deleteEvents(Collection<Long> eventIds) {
        if (CollectionUtils.isNotEmpty(eventIds)) {
            return Flux.concat(
                            eventRepository.deleteAllById(eventIds)
                                    .onErrorResume(e -> {
                                        log.error("Couldn't delete events from event service", e);
                                        return Mono.error(e);
                                    }),
                            userService.deleteEvents(eventIds)
                                    .onErrorResume(e -> {
                                        log.error("Couldn't delete events from user service", e);
                                        return Mono.error(e);
                                    }),
                            handleEventOnChange(eventIds)
                    )
                    .then();

        } else
            return Mono.error(() -> new RuntimeException("Missing event ids"));
    }

    private Mono<Void> handleEventOnChange(Collection<Long> eventIds) {
        return eventRepository.getEventsByContext(AlfEventContext.builder()
                        .eventIds(Sets.newHashSet(eventIds))
                        .build()
                )
                .flatMap(event -> {
                    String eventId = String.valueOf(event.getId());
                    scheduler.unscheduleJob(eventId, event.getEventName());
                    sinkRequester.tryEmitNext(AlfNotificationDto.builder()
                            .eventId(eventId)
                            .message(String.format("Event %s has been cancelled", event.getEventName()))
                            .build());
                    return Flux.empty();
                })
                .doOnError(e -> log.error("Couldn't unscheduled job. Cause: {}", e.getLocalizedMessage()))
                .then();
    }

    public Flux<AlfEventDto> getEventsByContext(AlfEventContext context) {
        if (Objects.nonNull(context)) {
            return eventRepository.getEventsByContext(context)
                    .map(e -> mapper.convertValue(e, AlfEventDto.class));
        } else
            return Flux.error(() -> new RuntimeException("Missing context"));
    }

    public Mono<Boolean> addParticipant(Long eventId) {
        return getEventsByContext(AlfEventContext.builder()
                .eventIds(Set.of(eventId))
                .build()
        )
                .map(AlfEventDto::getParticipants)
                .flatMap(numberOfParticipants -> updateParticipants(eventId, numberOfParticipants))
                .singleOrEmpty();
    }

    private Mono<Boolean> updateParticipants(Long eventId, int numberOfParticipants) {
        return eventRepository.addParticipant(eventId, numberOfParticipants);
    }


    public Flux<Void> closeEvent(Long eventId) {
        log.info("Close event with Id: {}", eventId);
        return Flux.concat(
                        eventRepository.closeEvent(eventId),
                        userService.deleteEvents(List.of(eventId))
                )
                .doOnError(e -> log.error("Couldn't close event with id {}", eventId));
    }

    public Flux<AlfEventDto> findAll() {
        return eventRepository.findAll()
                .map(e -> mapper.convertValue(e, AlfEventDto.class))
                .switchIfEmpty(Mono.defer(() -> {
                            log.warn("No events in db");
                            return Mono.empty();
                        }
                ));
    }
}

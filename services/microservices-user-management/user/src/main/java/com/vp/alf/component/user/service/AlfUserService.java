package com.vp.alf.component.user.service;

import com.vp.alf.common.controls.AlfNeo4jOperations;
import com.vp.alf.common.external_services.AlfEventRsocketService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import user.AlfUserDto;

import java.util.Objects;

@RequiredArgsConstructor
@Service
@Slf4j
public class AlfUserService {

    private final AlfNeo4jOperations operations;
    private final AlfEventRsocketService eventService;

    public Mono<String> createUser(AlfUserDto user) {
        assert Objects.nonNull(user) : "missing user payload";
        assert MapUtils.isNotEmpty(user.getUserProperty()) : "missing user property";

        return operations.createUser(user);
    }

    public Mono<Boolean> subscribeOnEvent(String userId, Long eventId) {
        assert StringUtils.isNotEmpty(userId) : "missing user id";
        assert Objects.nonNull(eventId) : "missing event id";

        return operations.subscribeOnEvent(userId, eventId)
                .doOnError(e -> log.error("Error occurred when try to subscribe on event"))
                .flatMap(eventService::addParticipant)
                .onErrorResume(e -> {
                    log.error("Couldn't add participant to event {}", eventId);
                    return operations.removeSubscription(userId, eventId);
                })
                .switchIfEmpty(Mono.defer(() -> {
                    log.error("Event with id {} or user with id {} not found", userId, eventId);
                    return Mono.just(Boolean.FALSE);
                }));
    }
}

package com.vp.alf.common.external_services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Collection;

@Component
@RequiredArgsConstructor
public class AlfUserService {

    @Qualifier("user")
    private final RSocketRequester rSocketRequester;
    private final AlfRsocketClient rSocketClient;

    public Mono<Void> createEvent(Long eventId) {
        String route = String.join(".", "user", "create", "event");
        return rSocketClient.sendRSocketFireAndForget(rSocketRequester, route, eventId);
    }

    public Mono<Void> deleteEvents(Collection<Long> eventIds) {
        String route = String.join(".", "user", "delete", "event");
        return rSocketClient.sendRSocketFireAndForget(rSocketRequester, route, eventIds);
    }
}

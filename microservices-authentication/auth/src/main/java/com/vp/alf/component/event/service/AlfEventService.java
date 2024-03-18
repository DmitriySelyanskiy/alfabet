package com.vp.alf.component.event.service;

import com.vp.alf.common.configuration.rsocket.AlfRsocketClient;
import event.AlfEventContext;
import event.AlfEventDto;
import event.AlfEventUpsertResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class AlfEventService {

    @Qualifier("event")
    private final RSocketRequester rSocketRequester;
    private final AlfRsocketClient rSocketClient;

    public Mono<AlfEventUpsertResponse> saveEvent(Collection<AlfEventDto> events) {
        String route = String.join(".", "event", "create");
        return rSocketClient.sendRSocketRequestResponse(rSocketRequester, route, events, AlfEventUpsertResponse.class);
    }

    public Mono<AlfEventUpsertResponse> updateEvent(Collection<AlfEventDto> events) {
        String route = String.join(".", "event", "update");
        return rSocketClient.sendRSocketRequestResponse(rSocketRequester, route, events, AlfEventUpsertResponse.class);
    }

    public Mono<Void> deleteEvents(Collection<Long> eventIds) {
        String route = String.join(".", "event", "delete");
        return rSocketClient.sendRSocketFireAndForget(rSocketRequester, route, eventIds);
    }

    public Flux<AlfEventDto> getEventsByContext(AlfEventContext context) {
        String route = String.join(".", "event", "byContext");
        return rSocketClient.sendRSocketRequestStream(rSocketRequester, route, context, AlfEventDto.class);
    }

    public Flux<AlfEventDto> findAll() {
        String route = String.join(".", "event", "findAll");
        return rSocketClient.sendRSocketRequestStream(rSocketRequester, route, AlfEventDto.class);
    }
}

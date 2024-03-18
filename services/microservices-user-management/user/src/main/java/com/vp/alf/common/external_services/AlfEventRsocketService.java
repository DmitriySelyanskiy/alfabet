package com.vp.alf.common.external_services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlfEventRsocketService {

    private final RSocketRequester rSocketRequester;

    private final AlfRsocketClient rSocketClient;


    public Mono<Boolean> addParticipant(Long eventId) {
        String route = String.join(".", "event","addParticipant", String.valueOf(eventId));
        return rSocketClient.sendRSocketRequestResponse(rSocketRequester, route, eventId, Boolean.class);
    }
}

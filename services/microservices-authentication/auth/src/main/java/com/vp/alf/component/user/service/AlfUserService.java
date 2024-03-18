package com.vp.alf.component.user.service;

import com.vp.alf.common.configuration.rsocket.AlfRsocketClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import user.AlfUserDto;

@Service
@RequiredArgsConstructor
public class AlfUserService {

    @Qualifier("user")
    private final RSocketRequester rSocketRequester;
    private final AlfRsocketClient rSocketClient;
    public Mono<String> createUser(AlfUserDto user) {
        String route = String.join(".", "user", "create");
        return rSocketClient.sendRSocketRequestResponse(rSocketRequester, route, user, String.class);
    }

    public Mono<Boolean> subscribeOnEvent(String userId, Long eventId) {
        String route = String.join(".", "user", "subscribe", userId);
        return rSocketClient.sendRSocketRequestResponse(rSocketRequester, route, eventId, Boolean.class);
    }


}

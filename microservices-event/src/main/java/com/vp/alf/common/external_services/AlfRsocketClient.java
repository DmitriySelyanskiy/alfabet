package com.vp.alf.common.external_services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class AlfRsocketClient {

    public <T>Mono<Void> sendRSocketFireAndForget(RSocketRequester rSocketRequester, String route, T requestBody) {
        log.debug("Connecting to external service via RSocket using route {}", route);
        return rSocketRequester
                .route(route)
                .data(requestBody)
                .send()
                .onErrorResume(e -> {
                    log.error("Request to external service via RSocket using route {} was unsuccessful. Cause: {}",
                            route, e.getLocalizedMessage());
                    return Mono.error(e);
                });

    }

}

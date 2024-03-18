package com.vp.alf.common.external_services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static reactor.core.Exceptions.isRetryExhausted;

@Component
@Slf4j
public class AlfRsocketClient {

    public <T> Mono<T> sendRSocketRequestResponse(RSocketRequester rSocketRequester, String route, Object requestBody, Class<T> dataType) {
        log.debug("Connecting to external service via RSocket using route {}", route);
        return rSocketRequester
                .route(route)
                .data(requestBody)
                .retrieveMono(dataType)
                .onErrorResume(this::handleRSocketRetryError)
                .switchIfEmpty(Mono.defer(() -> {
                    log.debug("Request to external service via RSocket using route {} was successful, but no data was collected. Request is: {}",
                            route, requestBody);
                    return Mono.empty();
                }));
    }

    private <T> Mono<? extends T> handleRSocketRetryError(Throwable rSocketError) {
        return Mono.error(isRetryExhausted(rSocketError) ? rSocketError.getCause() : rSocketError);
    }

}

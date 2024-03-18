package com.vp.alf.common.external_services;

import com.vp.alf.component.event.model.dto.AlfNotificationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.util.retry.Retry;

import java.time.Duration;

@Component
@RequiredArgsConstructor
@Slf4j
public class AlfEventNotification {

    @Qualifier("event")
    private final RSocketRequester rSocketRequester;

    private final AlfRsocketClient rSocketClient;
    @Qualifier("event-notification")
    private final Sinks.Many<AlfNotificationDto> sinksRequester;

    @Value("${spring.rsocket.duration}")
    private long duration;
    @Value("${spring.rsocket.attempts}")
    private long attempts;

    @Bean
    public void sendNotificationToSubscribers() {
        sinksRequester.asFlux()
                .flatMap(notification -> rSocketClient.sendRSocketFireAndForget(
                                        rSocketRequester,
                                        String.join(".", "notification.event", String.valueOf(notification.getEventId())),
                                        notification.getMessage()
                                )
                                .doOnError(error -> log.error("Error occurred while sending notification", error))
                                .retryWhen(Retry.fixedDelay(attempts, Duration.ofSeconds(duration)))
                                .onErrorResume(error -> {
                                    log.error("Failed to retry skipping data chunk", error);
                                    return Mono.empty();
                                })
                )
                .subscribe();
    }
}

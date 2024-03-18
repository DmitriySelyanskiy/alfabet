package com.vp.alf.common.config.rsocket;

import io.rsocket.frame.decoder.PayloadDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.rsocket.RSocketRequester;
import reactor.core.publisher.Sinks;
import reactor.util.retry.Retry;

import java.time.Duration;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class AlfRsocketConfig <T> {
    @Value("${spring.rsocket.user.client.host}")
    private String userHost;
    @Value("${spring.rsocket.user.client.port}")
    private int userPort;
    @Value("${spring.rsocket.notification.client.host}")
    private String notificationHost;
    @Value("${spring.rsocket.notification.client.port}")
    private int notificationPort;
    @Value("${spring.rsocket.duration}")
    private long duration;
    @Value("${spring.rsocket.attempts}")
    private long attempts;


    @Bean("user")
    RSocketRequester rSocketUserRequester(RSocketRequester.Builder builder) {
        return getRSocketRequester(userHost, userPort, builder);
    }

    @Bean("event")
    RSocketRequester rSocketNotificationRequester(RSocketRequester.Builder builder) {
        return getRSocketRequester(notificationHost, notificationPort, builder);
    }

    private RSocketRequester getRSocketRequester(String host, int port, RSocketRequester.Builder builder) {
        log.info("Build connection to microservice {} with host {} and port {}", host, host, port);
        return builder.
                rsocketConnector(connector -> connector
                        .payloadDecoder(PayloadDecoder.ZERO_COPY)
                        .reconnect(Retry.fixedDelay(attempts, Duration.ofSeconds(duration)))
                )
                .tcp(host, port);
    }

    @Bean("event-notification")
    Sinks.Many<T> sinksRequesterDataSource() {
        return Sinks.many().multicast().directAllOrNothing();
    }
}

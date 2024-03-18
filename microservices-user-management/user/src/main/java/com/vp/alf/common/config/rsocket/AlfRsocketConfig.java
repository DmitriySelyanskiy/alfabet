package com.vp.alf.common.config.rsocket;

import io.rsocket.frame.decoder.PayloadDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.rsocket.RSocketRequester;
import reactor.util.retry.Retry;

import java.time.Duration;

@Configuration
@Slf4j
public class AlfRsocketConfig {
    @Value("${spring.rsocket.event.client.host}")
    private String eventHost;
    @Value("${spring.rsocket.event.client.port}")
    private int eventPort;
    @Value("${spring.rsocket.duration}")
    private long duration;
    @Value("${spring.rsocket.attempts}")
    private long attempts;


    @Bean
    RSocketRequester rSocketUserRequester(RSocketRequester.Builder builder) {
        return getRSocketRequester(eventHost, eventPort, builder);
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
}

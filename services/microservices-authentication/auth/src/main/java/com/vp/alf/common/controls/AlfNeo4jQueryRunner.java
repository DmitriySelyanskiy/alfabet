package com.vp.alf.common.controls;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.reactivestreams.ReactiveResult;
import org.neo4j.driver.reactivestreams.ReactiveSession;
import org.slf4j.MarkerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class AlfNeo4jQueryRunner {

    private final static Integer retries = 3;

    private final static Retry simpleRetry = Retry.backoff(retries, Duration.ofSeconds(1)).doBeforeRetry((s) -> log.warn("Retrying after exception", s.failure()));

    private final Driver driver;

    public Flux<Record> readQuery(String query) {
        return Flux.usingWhen(
                        Mono.fromSupplier(() -> driver.session(ReactiveSession.class)),
                        session -> Flux.from(session.executeRead(tx -> Flux.from(tx.run(query))
                                .flatMap(ReactiveResult::records))),
                        ReactiveSession::close)
                .retryWhen(simpleRetry)
                .doOnError(error -> log.error(MarkerFactory.getMarker("readQuery"), "error", error));
    }

    public Mono<Record> readQuerySingle(String query) {
        return readQuery(query)
                .singleOrEmpty()
                .doOnError(
                        IndexOutOfBoundsException.class,
                        e -> log.error("Found more then one item when execute read query single {}", query));
    }

}

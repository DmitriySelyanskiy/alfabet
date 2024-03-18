package com.vp.alf.common.controls;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import user.AlfUserDto;

import java.util.Collection;

@Component
@RequiredArgsConstructor
@Slf4j
public class AlfNeo4jOperations {

    private final AlfNeo4jQueryRunner queryRunner;
    public Mono<String> createUser(AlfUserDto user) {
        return queryRunner.writeQuerySingle(AlfNeo4jTemplates.createUser(user))
                .map(record -> record.get("id").asString())
                .doOnError(e -> log.error("The user wasn't created. Cause: {}", e.getLocalizedMessage()));
    }

    public Mono<Long> subscribeOnEvent(String userId, Long eventId) {
        return queryRunner.writeQuerySingle(AlfNeo4jTemplates.subscribeOnEvent(userId, eventId))
                .map(record -> record.get("eventId").asLong());
    }

    public Mono<Boolean> removeSubscription(String userId, Long eventId) {
        return queryRunner.writeQuerySingle(AlfNeo4jTemplates.removeSubscription(userId, eventId))
                .map(record -> record.get("result").asBoolean());
    }

    public Mono<Long> createEvent(Long eventId) {
        return queryRunner.writeQuerySingle(AlfNeo4jTemplates.createEvent(eventId))
                .map(record -> record.get("eventId").asLong());
    }

    public Flux<Boolean> deleteEvents(Collection<Long> eventIds) {
        return queryRunner.writeQuery(AlfNeo4jTemplates.deleteEvents(eventIds))
                .map(record -> record.get("result").asBoolean());
    }
}

package com.vp.alf.component.event.repository;

import com.vp.alf.component.event.model.dao.AlfEventDao;
import event.AlfEventContext;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface AlfEventRepositoryOperation {

    Flux<AlfEventDao> getEventsByContext(AlfEventContext context);

    Mono<Boolean> addParticipant(Long eventId, int numberOfParticipants);

    Mono<Void> closeEvent(Long eventId);
}

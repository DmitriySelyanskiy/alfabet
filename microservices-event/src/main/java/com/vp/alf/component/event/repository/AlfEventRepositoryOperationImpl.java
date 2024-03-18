package com.vp.alf.component.event.repository;

import com.google.common.collect.Lists;
import com.vp.alf.component.event.model.dao.AlfEventDao;
import event.AlfEventContext;
import event.enums.AlfPropertyType;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.data.relational.core.query.Update;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

import static org.springframework.data.relational.core.query.Criteria.where;

@Component
@RequiredArgsConstructor
public class AlfEventRepositoryOperationImpl implements AlfEventRepositoryOperation {

    private final R2dbcEntityTemplate template;

    @Override
    public Flux<AlfEventDao> getEventsByContext(AlfEventContext context) {
        List<Criteria> criteria = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(context.getEventIds())) {
            criteria.add(where("id").in(context.getEventIds()));
        }
        if (StringUtils.isNotEmpty(context.getLocation())) {
            criteria.add(where("location").like(context.getLocation()));
        }
        if (StringUtils.isNotEmpty(context.getLocation())) {
            criteria.add(where("venue").like(context.getVenue()));
        }

        criteria.add(where("completed").is(false));

        AlfPropertyType type = Objects.nonNull(context.getType()) ? context.getType() : AlfPropertyType.createdAt;

        return template.select(AlfEventDao.class)
                .matching(Query.query(Criteria.from(criteria))
                        .sort(Sort.by(Sort.Direction.DESC, type.name())))
                .all();
    }

    @Override
    public Mono<Boolean> addParticipant(Long eventId, int numberOfParticipants) {
        numberOfParticipants++;
        return template.update(AlfEventDao.class)
                .matching(Query.query(where("id").is(eventId)))
                .apply(Update.update("participants", numberOfParticipants))
                .map(i ->  {
                    if (i > 0) {
                        return Boolean.TRUE;
                    } else
                        return Boolean.FALSE;
                });
    }

    @Override
    public Mono<Void> closeEvent(Long eventId) {
        return template.update(AlfEventDao.class)
                .matching(Query.query(where("id").is(eventId)))
                .apply(Update.update("completed", true))
                .handle((i, sink) ->  {
                    if (i > 0) {
                        sink.next(i);
                    } else
                       sink.error(new RuntimeException("Was not deleted from event db"));
                })
                .then();
    }
}

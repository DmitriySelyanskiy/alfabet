package com.vp.alf.common.controls;

import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.neo4j.cypherdsl.core.*;
import user.AlfUserDto;

import java.util.Collection;
import java.util.List;

import static org.neo4j.cypherdsl.core.Cypher.*;

public interface AlfNeo4jTemplates {

    static String createUser(AlfUserDto user) {
        MapExpression nodeProperties = mapOf("AlfId", randomUUID(), "Type", literalOf("User"));

        Node node = node("user").withProperties(nodeProperties).named("user");

        StatementBuilder.OrderableOngoingReadingAndWithWithoutWhere statement = create(node)
                .with("user");

        user.getUserProperty()
                .forEach((k, v) -> {
                    String propertySymbolicName = generateName();
                    Node property = node("property").withProperties("AlfId", literalOf(k.toString())).named(propertySymbolicName);

                    statement
                            .match(property)
                            .with(propertySymbolicName, "user")
                            .merge(anyNode("user")
                                    .relationshipTo(anyNode(propertySymbolicName), "Has").withProperties("AlfId", randomUUID(),
                                            "Value", literalOf(v)))
                            .with("user");
                });

        return statement
                .returning(
                        name("user").property("AlfId").as("id")
                )
                .build()
                .getCypher();
    }

    private static String generateName() {
        return RandomStringUtils.random(3, true, false);
    }

    static String subscribeOnEvent(String userId, Long eventId) {
        Node user = anyNode("user").withProperties("AlfId", literalOf(userId));
        Node event = anyNode("event").withProperties("AlfId", literalOf(eventId));
        Relationship relationship = user.relationshipTo(event, "Subscribe").withProperties("AlfId", randomUUID());
        return match(user, event)
                .create(relationship)
                .with("user", "event")
                .returning(name("event").property("AlfId").as("eventId"))
                .build()
                .getCypher();
    }

    static String removeSubscription(String userId, Long eventId) {
        Relationship relationship = anyNode("user").withProperties("AlfId", literalOf(userId))
                .relationshipTo(anyNode("event").withProperties("AlfId", literalOf(eventId))).named("rel");

        return match(relationship)
                .delete(name("rel"))
                .returning(literalFalse().as("result"))
                .build()
                .getCypher();
    }

    static String createEvent(Long eventId) {
        return create(anyNode("event")
                .withProperties("AlfId", literalOf(eventId), "Type", literalOf("Event")))
                .returning(name("event").property("AlfId").as("eventId"))
                .build()
                .getCypher();
    }

    static String deleteEvents(Collection<Long> eventIds) {
        List<Expression> ids = CollectionUtils.collect(eventIds, Cypher::literalOf, Lists.newArrayList());

        return match(anyNode("event"))
                .where(name("event").in(literalOf(ids)))
                .detachDelete("event")
                .returning(literalTrue().as("result"))
                .build()
                .getCypher();
    }
}

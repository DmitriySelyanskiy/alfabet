package com.vp.alf.component.user.utils;

import org.neo4j.cypherdsl.core.PatternElement;

import java.util.List;
import java.util.stream.Collectors;

import static org.neo4j.cypherdsl.core.Cypher.*;
import static org.neo4j.cypherdsl.core.Cypher.literalOf;

public interface AlfUserTemplateTest {

    static String createNewProperties(List<String> properties) {
        List<PatternElement> nodesProperty = properties.stream()
                .map(property -> node(property, List.of("property", "property"))
                        .withProperties("Type", literalOf("Property"),
                                "AlfId", literalOf(property)))
                .collect(Collectors.toList());

        return create(nodesProperty)
                .returning(literalTrue())
                .build()
                .getCypher();
    }
}

package com.vp.alf.common.controls;

import org.neo4j.cypherdsl.core.Relationship;

import static org.neo4j.cypherdsl.core.Cypher.*;

public interface AlfNeo4jTemplates {

    static String getUserByEmail(String email) {

        Relationship relationshipEmail = anyNode("user").relationshipTo(
                anyNode().withProperties("AlfId", literalOf("email"))).withProperties("Value", literalOf(email))
                .named("email");

        Relationship relationshipPassword = anyNode("user")
                .relationshipTo(anyNode().withProperties("AlfId", literalOf("password")))
                .named("password");

        Relationship relationshipPermission = anyNode("user")
                .relationshipTo(anyNode().withProperties("AlfId", literalOf("permissions")))
                .named("permission");

        return match(relationshipPassword, relationshipEmail, relationshipPermission)
                .returning(
                        name("email").property("Value").as("email"),
                        name("password").property("Value").as("password"),
                        name("permission").property("Value").as("permission")
                )
                .build()
                .getCypher();
    }
}

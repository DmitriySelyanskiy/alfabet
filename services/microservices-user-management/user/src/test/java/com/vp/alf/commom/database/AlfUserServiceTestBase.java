package com.vp.alf.commom.database;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.neo4j.driver.AuthToken;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.GraphDatabase;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;

import static com.vp.alf.commom.configuration.extensions.AlfNeo4jDBTestExtension.NEO_4_J_CONTAINER;

@Testcontainers
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Slf4j
public abstract class AlfUserServiceTestBase {

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        if (NEO_4_J_CONTAINER.isRunning()) {
            log.info("URL to connect to NEO4J {}", NEO_4_J_CONTAINER.getBoltUrl());
            registry.add("org.neo4j.driver.uri", NEO_4_J_CONTAINER::getBoltUrl);
            registry.add("org.neo4j.driver.authentication.username", () -> "neo4j");
            registry.add("org.neo4j.driver.authentication.password", NEO_4_J_CONTAINER::getAdminPassword);
        }
    }

    @AfterEach
    protected void cleanGraph() {
        if (NEO_4_J_CONTAINER.isRunning()) {
            String password = NEO_4_J_CONTAINER.getAdminPassword();

            AuthToken auth = AuthTokens.basic("neo4j", password);
            try (
                    var driver = GraphDatabase.driver(NEO_4_J_CONTAINER.getBoltUrl(), auth);
                    var session = driver.session()
            ) {
                session.run("match (n) detach delete n");
            }
        }
    }
}

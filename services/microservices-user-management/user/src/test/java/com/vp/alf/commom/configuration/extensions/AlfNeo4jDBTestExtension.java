package com.vp.alf.commom.configuration.extensions;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.neo4j.driver.AuthToken;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.testcontainers.containers.Neo4jContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.MountableFile;

import java.util.HashMap;

import static org.mockito.Mockito.mock;

@Configuration
public class AlfNeo4jDBTestExtension implements BeforeAllCallback, AfterAllCallback {


    @Override
    public void beforeAll(ExtensionContext extensionContext) {
        NEO_4_J_CONTAINER.start();
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) {
        if (!NEO_4_J_CONTAINER.isShouldBeReused()) {
            NEO_4_J_CONTAINER.stop();
        }
    }

    @Container
    public static final Neo4jContainer<?> NEO_4_J_CONTAINER = new Neo4jContainer<>("neo4j:5.9.0");

    @Bean
    @Primary
    public Driver createDriver() {
        if (NEO_4_J_CONTAINER.isRunning()) {
            String password = NEO_4_J_CONTAINER.getAdminPassword();
            AuthToken auth = AuthTokens.basic("neo4j", password);
            return GraphDatabase.driver(NEO_4_J_CONTAINER.getBoltUrl(), auth);
        }
        return mock(Driver.class);
    }
}

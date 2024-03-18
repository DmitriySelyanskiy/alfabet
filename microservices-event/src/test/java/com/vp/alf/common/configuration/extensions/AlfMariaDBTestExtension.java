package com.vp.alf.common.configuration.extensions;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.junit.jupiter.Container;

@Configuration
public class AlfMariaDBTestExtension implements BeforeAllCallback, AfterAllCallback {


    @Override
    public void beforeAll(ExtensionContext extensionContext) {
        MARIA_DB_CONTAINER.start();
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) {
        if (!MARIA_DB_CONTAINER.isShouldBeReused()) {
            MARIA_DB_CONTAINER.stop();
        }
    }

    @Container
    public static final MariaDBContainer<?> MARIA_DB_CONTAINER = new MariaDBContainer<>("mariadb:11.0.5")
            .withDatabaseName("alf")
            .withPassword("password")
            .withUsername("admin");
}

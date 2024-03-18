package com.vp.alf.common.configuration.database;

import lombok.extern.slf4j.Slf4j;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;

import static com.vp.alf.common.configuration.extensions.AlfMariaDBTestExtension.MARIA_DB_CONTAINER;

@Testcontainers
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Slf4j
public abstract class AlfEventServiceTestBase {

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        if (MARIA_DB_CONTAINER.isRunning()) {
            log.info("URL to connect to NEO4J {}", MARIA_DB_CONTAINER.getJdbcUrl());
            registry.add("spring.r2dbc.url", MARIA_DB_CONTAINER::getJdbcUrl);
            registry.add("spring.r2dbc.username", () -> "admin");
            registry.add("spring.r2dbc.password", () -> "password");
        }
    }

}

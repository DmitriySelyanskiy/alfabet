package com.vp.alf.common.configuration.database;

import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AlfNeo4jConfiguration {

    @Value("${org.neo4j.driver.uri}")
    private String uri;

    @Bean
    public Driver createNeo4jDriver() {
        return GraphDatabase.driver(uri);
    }
}

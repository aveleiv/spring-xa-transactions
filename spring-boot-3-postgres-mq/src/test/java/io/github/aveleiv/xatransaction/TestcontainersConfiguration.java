package io.github.aveleiv.xatransaction;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.activemq.ActiveMQContainer;
import org.testcontainers.containers.PostgreSQLContainer;

@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

    @Bean
    @ServiceConnection
    ActiveMQContainer activeMQContainer() {
        return new ActiveMQContainer("apache/activemq-classic:latest");
    }

    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgresContainer() {
        return new PostgreSQLContainer<>("postgres:latest")
                .withEnv("POSTGRES_INITDB_ARGS", "-c max_prepared_transactions=100")
                .withInitScript("sql/table.sql");
    }
}

package io.github.aveleiv.xatransaction;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.activemq.ActiveMQContainer;
import org.testcontainers.containers.PostgreSQLContainer;

public abstract class TestcontainersTestBase {

    private static ActiveMQContainer activeMQContainer() {
        return new ActiveMQContainer("apache/activemq-classic:latest");
    }

    private static PostgreSQLContainer<?> postgresContainer() {
        return new PostgreSQLContainer<>("postgres:latest")
                .withEnv("POSTGRES_INITDB_ARGS", "-c max_prepared_transactions=100")
                .withInitScript("sql/table.sql");
    }

    static ActiveMQContainer activeMQContainer = activeMQContainer();

    static PostgreSQLContainer<?> postgresContainer = postgresContainer();

    @BeforeAll
    static void startContainers() {
        activeMQContainer.start();
        postgresContainer.start();
    }

    @AfterAll
    static void stopContainers() {
        activeMQContainer.stop();
        postgresContainer.stop();
    }

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("postgres.url", postgresContainer::getJdbcUrl);
        registry.add("postgres.username", postgresContainer::getUsername);
        registry.add("postgres.password", postgresContainer::getPassword);

        registry.add("activemq.broker-url", activeMQContainer::getBrokerUrl);
    }
}

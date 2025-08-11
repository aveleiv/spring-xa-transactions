# Spring XA Transactions with Atomikos, PostgreSQL, and ActiveMQ

This repository demonstrates how to configure distributed (XA) transactions in Spring Boot using Atomikos to coordinate a relational database (PostgreSQL) and a message broker (ActiveMQ Classic) via Spring JMS. It contains two independent modules that show the setup for both Spring Boot 2 (javax.*) and Spring Boot 3 (jakarta.*).

- spring-boot-2-postgres-mq — Spring Boot 2.7, javax.transaction API, Atomikos starter for Boot 2
- spring-boot-3-postgres-mq — Spring Boot 3.5, jakarta.transaction API, Atomikos starter for Boot 3

Both modules implement the same domain: a Message is written to PostgreSQL and sent to a JMS destination in a single XA transaction. If either operation fails, the entire unit of work is rolled back.


## Why XA and Atomikos?

When your application spans multiple transactional resources (e.g., a database and a message queue), a simple local transaction is not enough. XA transactions provide two-phase commit (2PC) semantics across multiple resources so that either all changes commit or none do. Atomikos is a mature transaction manager that implements JTA and coordinates XA resources.

In this project:
- PostgreSQL is used through its XA-capable PGXADataSource
- ActiveMQ Classic is accessed through ActiveMQXAConnectionFactory
- Atomikos coordinates both via JTA (UserTransaction/TransactionManager), exposed to Spring as a PlatformTransactionManager


## Project structure

- buildSrc — a small Gradle convention plugin to share Java toolchain and test configuration (Java 21)
- spring-boot-2-postgres-mq — Boot 2 example (javax.*)
- spring-boot-3-postgres-mq — Boot 3 example (jakarta.*)

Shared dependency versions are managed in gradle/libs.versions.toml.


## Key concepts and code walkthrough

### Atomikos + JTA wiring (XaConfiguration)
- UserTransactionManager
- UserTransaction and TransactionManager exposed to Spring via JtaTransactionManager
- AtomikosDataSourceBean with org.postgresql.xa.PGXADataSource
- AtomikosConnectionFactoryBean wrapping ActiveMQXAConnectionFactory 

## Requirements

- JDK 21+
- Docker installed and running (required for Testcontainers)


## Building the project

At the repository root:
`./gradlew build`

This builds both modules and runs all tests with Testcontainers.

Build a single module:
- Boot 3 module: `./gradlew :spring-boot-3-postgres-mq:build`
- Boot 2 module: `./gradlew :spring-boot-2-postgres-mq:build`


## Running tests only

- All tests: `./gradlew test`
- Specific module: `./gradlew :spring-boot-3-postgres-mq:test`

Note: Tests start ephemeral PostgreSQL and ActiveMQ containers automatically.

## What to look at in the code

Spring Boot 3 module:
- `XaConfiguration` — Atomikos/JTA and XA resource setup (DataSource and JMS ConnectionFactory)
- `message/MessageService` — XA transactional methods sendAndSave and saveAndSend
- `message/MessageRepository` — simple `INSERT ... RETURNING` using `JdbcClient`
- `listener/MessageListener` — logs JMS messages to the messages queue
- `TestcontainersConfiguration` — containers defined with `@ServiceConnection` for Postgres and ActiveMQ
- `test/message/MessageServiceTest` — demonstrates commit/rollback behavior

Spring Boot 2 module:
- `XaConfiguration` — similar to Boot 3, but javax.* and custom `@ConfigurationProperties`
- `message/*` — analogous service/repository/message classes
- `TestcontainersTestBase` — starts containers and exposes connection properties via `@DynamicPropertySource`
- `test/message/MessageServiceTest` — similar assertions


## Dependency highlights

- Spring Boot 3.5.4 (Boot 3 module), Spring Boot 2.7.18 (Boot 2 module)
- Atomikos 6.x starters:
  - com.atomikos:transactions-spring-boot3-starter (Boot 3)
  - com.atomikos:transactions-spring-boot-starter (Boot 2)
- PostgreSQL JDBC and PGXADataSource
- ActiveMQ Classic XA ConnectionFactory
- Testcontainers: PostgreSQL, ActiveMQ, JUnit Jupiter
- assertj-db for DB assertions in tests

## References

- Atomikos documentation: https://www.atomikos.com/Documentation
- Spring Boot JMS (ActiveMQ Classic): https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#messaging.jms
- Spring Data JDBC/JdbcClient: https://docs.spring.io/spring-data/jdbc/docs/current/reference/html/
- Testcontainers: https://testcontainers.com/
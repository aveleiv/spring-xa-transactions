package io.github.aveleiv.xatransaction;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "postgres")
public record JdbcConnectionDetails(String url, String username, String password) {
}

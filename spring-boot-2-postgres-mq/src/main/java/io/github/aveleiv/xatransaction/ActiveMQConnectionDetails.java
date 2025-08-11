package io.github.aveleiv.xatransaction;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "activemq")
public record ActiveMQConnectionDetails(String brokerUrl) {
}

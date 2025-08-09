package io.github.aveleiv.xatransaction.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MessageListener {


    @JmsListener(destination = "messages")
    public void receiveMessage(String message) {
        log.atInfo()
                .addArgument(message)
                .log(() -> "Received message: {}");
    }
}

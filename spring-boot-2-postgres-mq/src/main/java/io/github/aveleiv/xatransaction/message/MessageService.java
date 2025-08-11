package io.github.aveleiv.xatransaction.message;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final JmsTemplate jms;

    MessageService(MessageRepository messageRepository, JmsTemplate jmsTemplate) {
        this.messageRepository = messageRepository;
        this.jms = jmsTemplate;
    }

    @Transactional(transactionManager = "xaTransactionManager")
    public void save(Message message) {
        log.info("Send message to jms: {}", message);
        jms.convertAndSend("messages", message.getMessage());

        log.info("Save message to database: {}", message);
        messageRepository.save(message);
    }
}

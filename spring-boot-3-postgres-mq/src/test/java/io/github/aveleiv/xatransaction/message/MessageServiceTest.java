package io.github.aveleiv.xatransaction.message;

import io.github.aveleiv.xatransaction.TestcontainersConfiguration;
import org.assertj.db.api.BDDAssertions;
import org.assertj.db.type.Changes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataAccessException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.destination.DestinationResolutionException;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import javax.sql.DataSource;

import static org.assertj.core.api.BDDAssertions.thenCode;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class MessageServiceTest {

    @Autowired
    private MessageService messageService;

    @Autowired
    private DataSource dataSource;

    @MockitoSpyBean
    private JmsTemplate jmsTemplate;

    @Test
    @DisplayName("Given a message, when it sent and saved, it should be present in database")
    public void testSendAndSave() {
        final var content = "Message content " + System.currentTimeMillis();
        final var id = 0;

        final var changes = new Changes(dataSource);
        changes.setStartPointNow();

        messageService.sendAndSave(new Message(id, content));

        changes.setEndPointNow();

        BDDAssertions.then(changes)
                .as("A new row should be created in the database")
                .hasNumberOfChanges(1)
                .ofCreation()
                .hasNumberOfChanges(1);

        BDDAssertions.then(changes)
                .change()
                .rowAtEndPoint()
                .value("message")
                .as("The specific message should be found in the database")
                .isEqualTo(content);
    }

    @Test
    @DisplayName("Given a message, when it saved twice, it should throw")
    public void testRepeatedSendAndSave() {
        final var content = "Message content " + System.currentTimeMillis();
        final var id = 1;

        final var changes = new Changes(dataSource);
        changes.setStartPointNow();

        thenCode(() -> messageService.sendAndSave(new Message(id, content)))
                .as("First save should be successful")
                .doesNotThrowAnyException();

        thenCode(() -> messageService.sendAndSave(new Message(id, content)))
                .as("Second save should throw")
                .isInstanceOf(DataAccessException.class);

        changes.setEndPointNow();

        BDDAssertions.then(changes)
                .as("A new row should be created in the database")
                .hasNumberOfChanges(1)
                .ofCreation()
                .hasNumberOfChanges(1);

        BDDAssertions.then(changes)
                .change()
                .rowAtEndPoint()
                .value("message")
                .as("The specific message should be found in the database")
                .isEqualTo(content);
    }

    @Test
    @DisplayName("Given a message, when it saved and sent, it should be present in database")
    public void testSaveAndSend() {
        final var content = "Message content " + System.currentTimeMillis();
        final var id = 2;

        final var changes = new Changes(dataSource);
        changes.setStartPointNow();

        messageService.saveAndSend(new Message(id, content));

        changes.setEndPointNow();

        BDDAssertions.then(changes)
                .as("A new row should be created in the database")
                .hasNumberOfChanges(1)
                .ofCreation()
                .hasNumberOfChanges(1);

        BDDAssertions.then(changes)
                .change()
                .rowAtEndPoint()
                .value("message")
                .as("The specific message should be found in the database")
                .isEqualTo(content);
    }

    @Test
    @DisplayName("Given a message, when it fail to send, it should throw")
    public void testRepeatedSaveAndSend() {
        final var content = "Message content " + System.currentTimeMillis();
        final var id = 3;

        BDDMockito.willThrow(new DestinationResolutionException("Failed to resolve destination"))
                .given(jmsTemplate).convertAndSend("messages", content);

        final var changes = new Changes(dataSource);
        changes.setStartPointNow();

        thenCode(() -> messageService.saveAndSend(new Message(id, content)))
                .as("Fails due to jms template's DestinationResolutionException")
                .isInstanceOf(DestinationResolutionException.class);

        changes.setEndPointNow();

        BDDAssertions.then(changes)
                .as("A new row should be created in the database")
                .hasNumberOfChanges(0)
                .ofCreation()
                .hasNumberOfChanges(0);
    }
}
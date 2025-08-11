package io.github.aveleiv.xatransaction.message;

import io.github.aveleiv.xatransaction.TestcontainersTestBase;
import org.assertj.db.api.BDDAssertions;
import org.assertj.db.type.Changes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;

import javax.sql.DataSource;

import static org.assertj.core.api.BDDAssertions.thenCode;

@SpringBootTest
class MessageServiceTest extends TestcontainersTestBase {

    @Autowired
    private MessageService messageService;

    @Autowired
    private DataSource dataSource;

    @Test
    @DisplayName("Given a message, when it saved, it should be present in database")
    public void testSave() {
        final var content = "Message content " + System.currentTimeMillis();
        final var id = 0;

        final var changes = new Changes(dataSource);
        changes.setStartPointNow();

        messageService.save(new Message(id, content));

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
    public void testReceive() {
        final var content = "Message content " + System.currentTimeMillis();
        final var id = 1;

        final var changes = new Changes(dataSource);
        changes.setStartPointNow();

        thenCode(() -> messageService.save(new Message(id, content)))
                .as("First save should be successful")
                .doesNotThrowAnyException();

        thenCode(() -> messageService.save(new Message(id, content)))
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
}
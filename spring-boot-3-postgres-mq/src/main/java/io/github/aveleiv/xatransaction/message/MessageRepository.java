package io.github.aveleiv.xatransaction.message;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
class MessageRepository {

    private final JdbcClient jdbc;

    public MessageRepository(JdbcClient jdbcClient) {
        this.jdbc = jdbcClient;
    }

    public Message save(Message message) {
        return jdbc.sql("""
                        INSERT INTO message (id, message) VALUES (:id, :message) RETURNING *
                        """)
                .paramSource(message)
                .query(Message.class)
                .single();
    }
}

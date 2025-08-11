package io.github.aveleiv.xatransaction.message;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
class MessageRepository {

    private static final BeanPropertyRowMapper<Message> MESSAGE_ROW_MAPPER = BeanPropertyRowMapper.newInstance(Message.class);

    private final NamedParameterJdbcTemplate jdbc;

    public MessageRepository(NamedParameterJdbcTemplate jdbcClient) {
        this.jdbc = jdbcClient;
    }

    public Message save(Message message) {
        return jdbc.query("""
                        INSERT INTO message (id, message) VALUES (:id, :message) RETURNING *
                        """,
                new BeanPropertySqlParameterSource(message), MESSAGE_ROW_MAPPER).getFirst();
    }
}

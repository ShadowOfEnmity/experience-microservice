package by.kostrikov.industry.persistence.model;

import jakarta.persistence.*;

import java.util.UUID;

@Table(name = "industry")
@Entity
public class Industry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 30, nullable = false, unique = true)
    private String name;

    @Column(columnDefinition = "UUID", name = "message_id")
    private UUID messageId;

    public Industry() {
    }

    public void setMessageId(UUID messageId) {
        this.messageId = messageId;
    }

    public UUID getMessageId() {
        return messageId;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

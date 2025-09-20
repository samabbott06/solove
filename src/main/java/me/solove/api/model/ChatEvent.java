package me.solove.api.model;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "chat_event")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ChatEvent {

  @Id
  @GeneratedValue
  @UuidGenerator
  private UUID id;

  @Column(name = "session_id", nullable = false)
  private UUID sessionId;

  @Enumerated(EnumType.STRING)
  @Column(name = "event_type", nullable = false)
  private EventType eventType;

  @Column(columnDefinition = "text")
  private String content;

  @JdbcTypeCode(SqlTypes.JSON)
  private Map<String, Object> metadata;

  @Column(name = "created_at", nullable = false, updatable = false,
          columnDefinition = "timestamptz default now()")
  private OffsetDateTime createdAt;

  public enum EventType {
    USER_MESSAGE,
    ASSISTANT_MESSAGE,
    SESSION_START,
    SESSION_END,
    FUNCTION_CALL,
    ERROR
  }

  @PrePersist
  protected void onCreate() {
    if (createdAt == null) {
      createdAt = OffsetDateTime.now();
    }
  }
}
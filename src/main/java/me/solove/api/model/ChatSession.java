package me.solove.api.model;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "chat_session")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ChatSession {

  @Id
  @GeneratedValue
  @UuidGenerator
  private UUID id;

  @Column(name = "user_id", nullable = false)
  private UUID userId;

  @Enumerated(EnumType.STRING)
  @Column(name = "session_type", nullable = false)
  private SessionType sessionType;

  @Column(name = "started_at", nullable = false, updatable = false,
          columnDefinition = "timestamptz default now()")
  private OffsetDateTime startedAt;

  @Column(name = "ended_at")
  private OffsetDateTime endedAt;

  @Column(name = "created_at", nullable = false, updatable = false,
          columnDefinition = "timestamptz default now()")
  private OffsetDateTime createdAt;

  @Column(name = "updated_at", nullable = false,
          columnDefinition = "timestamptz default now()")
  private OffsetDateTime updatedAt;

  public enum SessionType {
    TEXT, VOICE
  }

  @PrePersist
  protected void onCreate() {
    if (startedAt == null) {
      startedAt = OffsetDateTime.now();
    }
    if (createdAt == null) {
      createdAt = OffsetDateTime.now();
    }
    if (updatedAt == null) {
      updatedAt = OffsetDateTime.now();
    }
  }

  @PreUpdate
  protected void onUpdate() {
    updatedAt = OffsetDateTime.now();
  }
}
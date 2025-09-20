package me.solove.api.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import me.solove.api.model.ChatSession;
import me.solove.api.model.ChatEvent;

import java.time.OffsetDateTime;
import java.util.UUID;

public class ChatDtos {

  public record StartSessionRequest() {}

  public record SessionResponse(
      UUID id,
      String sessionType,
      OffsetDateTime startedAt,
      OffsetDateTime endedAt
  ) {
    public static SessionResponse of(ChatSession session) {
      return new SessionResponse(
          session.getId(),
          session.getSessionType().name().toLowerCase(),
          session.getStartedAt(),
          session.getEndedAt()
      );
    }
  }

  public record SendMessageRequest(
      @NotBlank(message = "Message cannot be blank")
      @Size(max = 4000, message = "Message too long")
      String message
  ) {}

  public record MessageResponse(
      String response
  ) {}

  public record ConversationEvent(
      String type,
      String content,
      OffsetDateTime timestamp
  ) {
    public static ConversationEvent of(ChatEvent event) {
      return new ConversationEvent(
          event.getEventType().name().toLowerCase(),
          event.getContent(),
          event.getCreatedAt()
      );
    }
  }

  public record ConversationHistoryResponse(
      UUID sessionId,
      java.util.List<ConversationEvent> events
  ) {}
}
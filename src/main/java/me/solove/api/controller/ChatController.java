package me.solove.api.controller;

import jakarta.validation.Valid;
import me.solove.api.service.ChatService;
import me.solove.api.web.dto.ChatDtos.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/chat")
public class ChatController {

  private final ChatService chatService;

  public ChatController(ChatService chatService) {
    this.chatService = chatService;
  }

  @PostMapping("/sessions")
  public ResponseEntity<SessionResponse> startSession(@Valid @RequestBody StartSessionRequest request) {
    // For testing - use hardcoded test user
    var userId = getUserIdFromExternalId("test-user-external-id");

    var session = chatService.startTextSession(userId);
    return ResponseEntity.ok(SessionResponse.of(session));
  }

  @PostMapping("/sessions/{sessionId}/messages")
  public ResponseEntity<MessageResponse> sendMessage(@PathVariable UUID sessionId,
                                                     @Valid @RequestBody SendMessageRequest request) {
    // For testing - use hardcoded test user
    var userId = getUserIdFromExternalId("test-user-external-id");

    var response = chatService.sendMessage(userId, sessionId, request.message());
    return ResponseEntity.ok(new MessageResponse(response));
  }

  @PostMapping("/sessions/{sessionId}/end")
  public ResponseEntity<Void> endSession(@PathVariable UUID sessionId) {
    // For testing - use hardcoded test user
    var userId = getUserIdFromExternalId("test-user-external-id");

    chatService.endSession(userId, sessionId);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/sessions")
  public ResponseEntity<java.util.List<SessionResponse>> getSessions() {
    // For testing - use hardcoded test user
    var userId = getUserIdFromExternalId("test-user-external-id");

    var sessions = chatService.getUserSessions(userId);
    var response = sessions.stream()
        .map(SessionResponse::of)
        .collect(Collectors.toList());

    return ResponseEntity.ok(response);
  }

  @GetMapping("/sessions/{sessionId}/history")
  public ResponseEntity<ConversationHistoryResponse> getSessionHistory(@PathVariable UUID sessionId) {
    // For testing - use hardcoded test user
    var userId = getUserIdFromExternalId("test-user-external-id");

    var events = chatService.getSessionHistory(userId, sessionId);
    var eventDtos = events.stream()
        .map(ConversationEvent::of)
        .collect(Collectors.toList());

    return ResponseEntity.ok(new ConversationHistoryResponse(sessionId, eventDtos));
  }

  private UUID getUserIdFromExternalId(String externalId) {
    // For testing - hardcoded test user UUID that matches our migration
    return UUID.fromString("00000000-0000-0000-0000-000000000001");
  }
}
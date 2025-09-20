package me.solove.api.service;

import me.solove.api.model.ChatEvent;
import me.solove.api.model.ChatSession;
import me.solove.api.repository.ChatEventRepository;
import me.solove.api.repository.ChatSessionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ChatService {

  private static final Logger logger = LoggerFactory.getLogger(ChatService.class);

  private final ChatModel chatModel;
  private final ChatMemory chatMemory;
  private final ChatSessionRepository sessionRepository;
  private final ChatEventRepository eventRepository;
  private final SystemPromptService systemPromptService;

  public ChatService(ChatModel chatModel,
                     ChatMemory chatMemory,
                     ChatSessionRepository sessionRepository,
                     ChatEventRepository eventRepository,
                     SystemPromptService systemPromptService) {
    this.chatModel = chatModel;
    this.chatMemory = chatMemory;
    this.sessionRepository = sessionRepository;
    this.eventRepository = eventRepository;
    this.systemPromptService = systemPromptService;
  }

  @Transactional
  public ChatSession startTextSession(UUID userId) {
    var session = ChatSession.builder()
        .userId(userId)
        .sessionType(ChatSession.SessionType.TEXT)
        .startedAt(OffsetDateTime.now())
        .build();

    session = sessionRepository.save(session);

    // Log session start event
    var startEvent = ChatEvent.builder()
        .sessionId(session.getId())
        .eventType(ChatEvent.EventType.SESSION_START)
        .content("Text chat session started")
        .build();
    eventRepository.save(startEvent);

    logger.info("Started text chat session {} for user {}", session.getId(), userId);
    return session;
  }

  @Transactional
  public String sendMessage(UUID userId, UUID sessionId, String userMessage) {
    // Verify user owns this session
    var session = sessionRepository.findUserSession(userId, sessionId)
        .orElseThrow(() -> new IllegalArgumentException("Session not found or access denied"));

    String conversationId = sessionId.toString();

    // Log user message for analytics
    var userEvent = ChatEvent.builder()
        .sessionId(sessionId)
        .eventType(ChatEvent.EventType.USER_MESSAGE)
        .content(userMessage)
        .build();
    eventRepository.save(userEvent);

    try {
      // Add user message to Spring AI memory
      var userMsg = new UserMessage(userMessage);
      chatMemory.add(conversationId, userMsg);

      // Get conversation from Spring AI memory (includes system prompt automatically)
      List<Message> messages = new ArrayList<>();
      messages.add(new SystemMessage(systemPromptService.getTherapeuticSystemPrompt()));
      messages.addAll(chatMemory.get(conversationId));

      // Get AI response
      var prompt = new Prompt(messages);
      var response = chatModel.call(prompt);
      String aiResponse = response.getResult().getOutput().getContent();

      // Add assistant response to Spring AI memory
      var assistantMsg = new AssistantMessage(aiResponse);
      chatMemory.add(conversationId, assistantMsg);

      // Log assistant response for analytics
      var assistantEvent = ChatEvent.builder()
          .sessionId(sessionId)
          .eventType(ChatEvent.EventType.ASSISTANT_MESSAGE)
          .content(aiResponse)
          .build();
      eventRepository.save(assistantEvent);

      logger.info("Processed message in session {} for user {}", sessionId, userId);
      return aiResponse;

    } catch (Exception e) {
      logger.error("Error processing message in session {} for user {}: {}", sessionId, userId, e.getMessage(), e);

      // Log error event for analytics
      var errorEvent = ChatEvent.builder()
          .sessionId(sessionId)
          .eventType(ChatEvent.EventType.ERROR)
          .content("Error processing message: " + e.getMessage())
          .build();
      eventRepository.save(errorEvent);

      throw new RuntimeException("Failed to process message", e);
    }
  }

  @Transactional
  public void endSession(UUID userId, UUID sessionId) {
    var session = sessionRepository.findUserSession(userId, sessionId)
        .orElseThrow(() -> new IllegalArgumentException("Session not found or access denied"));

    String conversationId = sessionId.toString();

    session.setEndedAt(OffsetDateTime.now());
    sessionRepository.save(session);

    // Clear Spring AI memory for this conversation
    chatMemory.clear(conversationId);

    // Log session end event for analytics
    var endEvent = ChatEvent.builder()
        .sessionId(sessionId)
        .eventType(ChatEvent.EventType.SESSION_END)
        .content("Session ended")
        .build();
    eventRepository.save(endEvent);

    logger.info("Ended chat session {} for user {}", sessionId, userId);
  }

  public List<ChatSession> getUserSessions(UUID userId) {
    return sessionRepository.findByUserIdOrderByStartedAtDesc(userId,
        org.springframework.data.domain.PageRequest.of(0, 50)).getContent();
  }

  public List<ChatEvent> getSessionHistory(UUID userId, UUID sessionId) {
    // Verify user owns this session
    sessionRepository.findUserSession(userId, sessionId)
        .orElseThrow(() -> new IllegalArgumentException("Session not found or access denied"));

    return eventRepository.findConversation(sessionId);
  }

}
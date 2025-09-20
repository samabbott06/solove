package me.solove.api.repository;

import me.solove.api.model.ChatEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChatEventRepository extends JpaRepository<ChatEvent, UUID> {

  @Query("SELECT ce FROM ChatEvent ce WHERE ce.sessionId = :sessionId ORDER BY ce.createdAt")
  List<ChatEvent> findSessionEvents(@Param("sessionId") UUID sessionId);

  @Query("SELECT ce FROM ChatEvent ce WHERE ce.sessionId = :sessionId ORDER BY ce.createdAt")
  Page<ChatEvent> findSessionEvents(@Param("sessionId") UUID sessionId, Pageable pageable);

  @Query("SELECT ce FROM ChatEvent ce WHERE ce.sessionId = :sessionId AND ce.eventType IN ('USER_MESSAGE', 'ASSISTANT_MESSAGE') ORDER BY ce.createdAt")
  List<ChatEvent> findConversation(@Param("sessionId") UUID sessionId);
}
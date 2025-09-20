package me.solove.api.repository;

import me.solove.api.model.ChatSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChatSessionRepository extends JpaRepository<ChatSession, UUID> {

  Page<ChatSession> findByUserIdOrderByStartedAtDesc(UUID userId, Pageable pageable);

  @Query("SELECT cs FROM ChatSession cs WHERE cs.userId = :userId AND cs.endedAt IS NULL ORDER BY cs.startedAt DESC")
  List<ChatSession> findActiveSessions(@Param("userId") UUID userId);

  @Query("SELECT cs FROM ChatSession cs WHERE cs.userId = :userId AND cs.id = :sessionId")
  Optional<ChatSession> findUserSession(@Param("userId") UUID userId, @Param("sessionId") UUID sessionId);
}
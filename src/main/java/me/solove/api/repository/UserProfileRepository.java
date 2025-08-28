package me.solove.api.repository;

import me.solove.api.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {
  Optional<UserProfile> findByExternalId(String externalId);
  Optional<UserProfile> findByEmail(String email);
  // small perf trick for existence checks
  boolean existsByExternalId(String externalId);
}

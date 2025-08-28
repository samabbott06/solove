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
@Table(name = "user_profile")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserProfile {
  @Id
  @GeneratedValue
  @UuidGenerator
  private UUID id;

  @Column(name = "external_id", nullable = false, unique = true)
  private String externalId;  // JWT sub

  @Column(nullable = false, unique = true)
  private String email;

  @Column(name = "display_name")
  private String displayName;

  @Column(name = "created_at", nullable = false, updatable = false,
          columnDefinition = "timestamptz default now()")
  private OffsetDateTime createdAt;

  @Column(name = "updated_at", nullable = false,
          columnDefinition = "timestamptz default now()")
  private OffsetDateTime updatedAt;

  /* getters/setters/constructors */

  // public UserProfile() {}

  // public UserProfile(String externalId, String email, String displayName) {
  //   this.externalId = externalId;
  //   this.email = email;
  //   this.displayName = displayName;
  // }

  // public UUID getId() { return id; }
  // public String getExternalId() { return externalId; }
  // public void setExternalId(String externalId) { this.externalId = externalId; }
  // public String getEmail() { return email; }
  // public void setEmail(String email) { this.email = email; }
  // public String getDisplayName() { return displayName; }
  // public void setDisplayName(String displayName) { this.displayName = displayName; }
  // public OffsetDateTime getCreatedAt() { return createdAt; }
  // public OffsetDateTime getUpdatedAt() { return updatedAt; }
}

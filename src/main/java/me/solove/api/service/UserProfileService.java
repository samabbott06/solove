package me.solove.api.service;

import me.solove.api.model.UserProfile;
import me.solove.api.repository.UserProfileRepository;
import me.solove.api.web.dto.UserProfileDtos.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserProfileService {

  private final UserProfileRepository repo;

  public UserProfileService(UserProfileRepository repo) {
    this.repo = repo;
  }

  /** Idempotent upsert by externalId. */
  @Transactional
  public UserProfile createOrUpdateUserProfile(UpsertRequest req) {
    var existing = repo.findByExternalId(req.externalId()).orElse(null);
    if (existing == null) {
      var created = UserProfile.builder()
              .externalId(req.externalId())
              .email(req.email())
              .displayName(req.displayName())
              .build();
      return repo.save(created);
    }
    // update mutable fields
    if (req.displayName() != null) existing.setDisplayName(req.displayName());
    if (req.email() != null)      existing.setEmail(req.email());
    return repo.save(existing);
  }

  @Transactional(readOnly = true)
  public UserProfile getByExternalId(String externalId) {
    return repo.findByExternalId(externalId).orElseThrow(() -> new IllegalStateException("Profile not found for externalId=" + externalId));
  }

  @Transactional
  public UserProfile updateDisplayName(String externalId, String displayName) {
    var profile = getByExternalId(externalId);
    profile.setDisplayName(displayName);
    return profile;
  }
}

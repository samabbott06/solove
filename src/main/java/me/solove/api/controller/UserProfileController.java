package me.solove.api.controller;

import jakarta.validation.Valid;
import me.solove.api.model.UserProfile;
import me.solove.api.service.UserProfileService;
import me.solove.api.web.dto.UserProfileDtos.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/profiles")
public class UserProfileController {

  private final UserProfileService service;

  public UserProfileController(UserProfileService service) {
    this.service = service;
  }

  /** Idempotent upsert by externalId (useful for back-office tools and JIT-provisioning). */
  @PostMapping
  public ResponseEntity<Response> createOrUpdateUserProfile(@Valid @RequestBody UpsertRequest body) {
    var saved = service.createOrUpdateUserProfile(body);
    return ResponseEntity.status(HttpStatus.OK).body(toDto(saved));
  }

  /** Get my profile (from JWT). */
  @GetMapping("/me")
  public ResponseEntity<Response> me(@AuthenticationPrincipal Jwt jwt) {
    var profile = service.getByExternalId(jwt.getSubject());
    return ResponseEntity.ok(toDto(profile));
  }

  /** Update mutable fields on my profile. */
  @PutMapping("/me")
  public ResponseEntity<Response> updateMe(@AuthenticationPrincipal Jwt jwt,
                                           @Valid @RequestBody UpdateMeRequest body) {
    var updated = service.updateDisplayName(jwt.getSubject(), body.displayName());
    return ResponseEntity.ok(toDto(updated));
  }

  private Response toDto(UserProfile p) {
    return Response.of(p.getId(), p.getExternalId(), p.getEmail(), p.getDisplayName());
  }
}

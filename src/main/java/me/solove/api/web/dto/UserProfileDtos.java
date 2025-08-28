package me.solove.api.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public class UserProfileDtos {

  public record UpsertRequest(
      @NotBlank String externalId,
      @NotBlank @Email String email,
      String displayName
  ) {}

  public record UpdateMeRequest(
      String displayName
  ) {}

  public record Response(
      UUID id,
      String externalId,
      String email,
      String displayName
  ) {
    public static Response of(UUID id, String externalId, String email, String displayName) {
      return new Response(id, externalId, email, displayName);
    }
  }
}

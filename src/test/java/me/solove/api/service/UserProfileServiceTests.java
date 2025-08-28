package me.solove.api.service;

import me.solove.api.model.UserProfile;
import me.solove.api.repository.UserProfileRepository;
import me.solove.api.web.dto.UserProfileDtos;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceTests {

    @Mock
    private UserProfileRepository userProfileRepository;

    @InjectMocks
    private UserProfileService userProfileService;

    private UserProfileDtos.UpsertRequest upsertRequest;
    private UserProfile userProfile;

    @BeforeEach
    void setUp() {
        upsertRequest = new UserProfileDtos.UpsertRequest(
                "externalId123",
                "test@example.com",
                "Test User"
        );
        userProfile = UserProfile.builder()
                .id(UUID.randomUUID())
                .externalId("externalId123")
                .email("test@example.com")
                .displayName("Test User")
                .build();
    }

    /**
     * Tests that a new user profile is created when the external ID does not exist.
     */
    @Test
    void testCreateUserProfile() {
        // Given
        when(userProfileRepository.findByExternalId(upsertRequest.externalId())).thenReturn(Optional.empty());
        when(userProfileRepository.save(any(UserProfile.class))).thenReturn(userProfile);

        // When
        UserProfile result = userProfileService.createOrUpdateUserProfile(upsertRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getExternalId()).isEqualTo(upsertRequest.externalId());
        assertThat(result.getEmail()).isEqualTo(upsertRequest.email());
        assertThat(result.getDisplayName()).isEqualTo(upsertRequest.displayName());
        verify(userProfileRepository).save(any(UserProfile.class));
    }

    /**
     * Tests that an existing user profile is updated when the external ID already exists.
     */
    @Test
    void testUpdateUserProfile() {
        // Given
        UserProfile existingUserProfile = UserProfile.builder()
                .id(UUID.randomUUID())
                .externalId("externalId123")
                .email("old@example.com")
                .displayName("Old Name")
                .build();

        UserProfileDtos.UpsertRequest updateRequest = new UserProfileDtos.UpsertRequest(
                "externalId123",
                "new@example.com",
                "New Name"
        );

        when(userProfileRepository.findByExternalId(updateRequest.externalId())).thenReturn(Optional.of(existingUserProfile));
        when(userProfileRepository.save(any(UserProfile.class))).thenAnswer(invocation -> {
            UserProfile user = invocation.getArgument(0);
            user.setEmail(updateRequest.email());
            user.setDisplayName(updateRequest.displayName());
            return user;
        });

        // When
        UserProfile result = userProfileService.createOrUpdateUserProfile(updateRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getExternalId()).isEqualTo(updateRequest.externalId());
        assertThat(result.getEmail()).isEqualTo(updateRequest.email());
        assertThat(result.getDisplayName()).isEqualTo(updateRequest.displayName());
        verify(userProfileRepository).save(any(UserProfile.class));
    }
}

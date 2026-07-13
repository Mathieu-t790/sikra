package api.sikra.app.model;

import java.time.Instant;
import java.util.UUID;
import lombok.Builder;

@Builder
public record FileSubmission(
    UUID id, String fileKey, String fileName, UUID userId, Instant createdAt) {}

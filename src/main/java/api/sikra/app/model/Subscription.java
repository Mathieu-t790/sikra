package api.sikra.app.model;

import java.time.Instant;
import java.util.UUID;
import lombok.Builder;

@Builder
public record Subscription(
    UUID id, Instant createdAt, SubscriptionStatus status, UUID courseId, UUID userId) {}

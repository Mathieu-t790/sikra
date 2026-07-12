package api.sikra.app.model;

import java.time.Instant;
import java.util.UUID;
import lombok.Builder;

@Builder
public record EmailHistory(
    UUID id,
    UUID subscriptionId,
    String recipient,
    String subject,
    EmailStatus status,
    String errorMessage,
    Instant sentAt) {}

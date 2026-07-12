package api.sikra.app.model;

import java.time.Instant;
import java.util.UUID;
import lombok.Builder;

@Builder
public record Course(UUID id, String title, Instant start, Instant end) {}

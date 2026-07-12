package api.sikra.app.model;

import java.util.UUID;
import lombok.Builder;

@Builder
public record Student(UUID id, User user, String reference, String level) {}

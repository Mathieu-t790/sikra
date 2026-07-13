package api.sikra.app.model;

import java.util.UUID;
import lombok.Builder;

@Builder
public record User(UUID id, String firstName, String lastName, String userName, String email) {}

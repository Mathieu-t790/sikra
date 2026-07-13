package api.sikra.app.endpoint.rest.controller.dto;

import java.util.UUID;
import lombok.Builder;

@Builder
public record FileSubmissionRequest(UUID userId) {}

package api.sikra.app.endpoint.rest.controller.dto;

import api.sikra.app.model.FileSubmission;
import java.time.Instant;
import java.util.UUID;
import lombok.Builder;

@Builder
public record FileSubmissionResponse(
    UUID id, String fileName, UUID userId, Instant createdAt, String message) {

  public static FileSubmissionResponse from(FileSubmission submission) {
    return FileSubmissionResponse.builder()
        .id(submission.id())
        .fileName(submission.fileName())
        .userId(submission.userId())
        .createdAt(submission.createdAt())
        .build();
  }

  public static FileSubmissionResponse withMessage(FileSubmission submission, String message) {
    return FileSubmissionResponse.builder()
        .id(submission.id())
        .fileName(submission.fileName())
        .userId(submission.userId())
        .createdAt(submission.createdAt())
        .message(message)
        .build();
  }
}

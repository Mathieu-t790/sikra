package api.sikra.app.endpoint.rest.controller.dto;

import api.sikra.app.model.FileSubmission;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.time.Instant;
import java.util.UUID;
import lombok.Builder;

@Builder
public record FileSubmissionResponse(
    UUID id,
    String fileName,
    String email,
    Instant createdAt,
    @JsonInclude(Include.NON_NULL) String message) {

  public static FileSubmissionResponse from(FileSubmission submission) {
    return FileSubmissionResponse.builder()
        .id(submission.id())
        .fileName(submission.fileName())
        .email(submission.email())
        .createdAt(submission.createdAt())
        .build();
  }

  public static FileSubmissionResponse withMessage(FileSubmission submission, String message) {
    return FileSubmissionResponse.builder()
        .id(submission.id())
        .fileName(submission.fileName())
        .email(submission.email())
        .createdAt(submission.createdAt())
        .message(message)
        .build();
  }
}

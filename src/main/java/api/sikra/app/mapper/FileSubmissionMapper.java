package api.sikra.app.mapper;

import api.sikra.app.model.FileSubmission;
import api.sikra.app.repository.model.JFileSubmission;
import org.springframework.stereotype.Component;

@Component
public class FileSubmissionMapper {

  public FileSubmission toModel(JFileSubmission entity) {
    return FileSubmission.builder()
        .id(entity.getId())
        .fileKey(entity.getFileKey())
        .fileName(entity.getFileName())
        .email(entity.getEmail())
        .createdAt(entity.getCreatedAt())
        .build();
  }

  public JFileSubmission toEntity(FileSubmission model) {
    return JFileSubmission.builder()
        .id(model.id())
        .fileKey(model.fileKey())
        .fileName(model.fileName())
        .email(model.email())
        .createdAt(model.createdAt())
        .build();
  }
}

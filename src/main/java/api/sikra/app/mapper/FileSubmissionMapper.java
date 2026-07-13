package api.sikra.app.mapper;

import api.sikra.app.model.FileSubmission;
import api.sikra.app.repository.model.JFileSubmission;
import api.sikra.app.repository.model.JUser;
import org.springframework.stereotype.Component;

@Component
public class FileSubmissionMapper {

  public FileSubmission toModel(JFileSubmission entity) {
    return FileSubmission.builder()
        .id(entity.getId())
        .fileKey(entity.getFileKey())
        .fileName(entity.getFileName())
        .userId(entity.getUser().getId())
        .email(entity.getUser().getEmail())
        .createdAt(entity.getCreatedAt())
        .build();
  }

  public JFileSubmission toEntity(FileSubmission model, JUser userEntity) {
    return JFileSubmission.builder()
        .id(model.id())
        .fileKey(model.fileKey())
        .fileName(model.fileName())
        .user(userEntity)
        .createdAt(model.createdAt())
        .build();
  }
}

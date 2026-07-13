package api.sikra.app.service;

import api.sikra.app.endpoint.event.EventProducer;
import api.sikra.app.endpoint.event.model.FileSubmitted;
import api.sikra.app.file.bucket.BucketComponent;
import api.sikra.app.mapper.FileSubmissionMapper;
import api.sikra.app.model.FileSubmission;
import api.sikra.app.repository.FileSubmissionRepository;
import api.sikra.app.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.File;
import java.io.InputStream;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import javax.imageio.ImageIO;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@AllArgsConstructor
public class FileSubmissionService {

  private final FileSubmissionRepository repository;
  private final FileSubmissionMapper mapper;
  private final BucketComponent bucketComponent;
  private final EventProducer<FileSubmitted> eventProducer;
  private final UserRepository userRepository;

  @SneakyThrows
  public FileSubmission create(MultipartFile multipartFile, UUID userId) {
    var userEntity =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));

    var tempFile = convertToGrayScale(multipartFile.getInputStream());
    var submissionId = UUID.randomUUID();
    var bucketKey = buildBucketKey(submissionId);

    uploadToBucket(tempFile, bucketKey);

    var submission = buildSubmission(submissionId, bucketKey, multipartFile.getOriginalFilename(), userId);
    var saved = mapper.toModel(repository.save(mapper.toEntity(submission, userEntity)));

    eventProducer.accept(List.of(new FileSubmitted(saved.id())));
    return saved;
  }

  @SneakyThrows
  private File convertToGrayScale(InputStream inputStream) {
    var original = ImageIO.read(inputStream);
    var gray =
        new BufferedImage(original.getWidth(), original.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
    var op = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
    op.filter(original, gray);

    var tempFile = File.createTempFile("upload-", ".jpg");
    ImageIO.write(gray, "jpg", tempFile);
    return tempFile;
  }

  private String buildBucketKey(UUID submissionId) {
    return "file-submissions/" + submissionId + "/image.jpg";
  }

  private void uploadToBucket(File file, String bucketKey) {
    bucketComponent.upload(file, bucketKey);
    file.delete();
  }

  private FileSubmission buildSubmission(
      UUID id, String bucketKey, String fileName, UUID userId) {
    return FileSubmission.builder()
        .id(id)
        .fileKey(bucketKey)
        .fileName(fileName)
        .userId(userId)
        .createdAt(Instant.now())
        .build();
  }

  public Page<FileSubmission> getAll(int offset, int limit) {
    var pageable =
        PageRequest.of(offset / limit, limit, Sort.Direction.DESC, "createdAt");
    return repository.findAll(pageable).map(mapper::toModel);
  }
}

package api.sikra.app.service;

import api.sikra.app.endpoint.event.EventProducer;
import api.sikra.app.endpoint.event.model.FileSubmitted;
import api.sikra.app.file.bucket.BucketComponent;
import api.sikra.app.mapper.FileSubmissionMapper;
import api.sikra.app.model.FileSubmission;
import api.sikra.app.repository.FileSubmissionRepository;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.File;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import javax.imageio.ImageIO;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@AllArgsConstructor
public class FileSubmissionService {

  private final FileSubmissionRepository repository;
  private final FileSubmissionMapper mapper;
  private final BucketComponent bucketComponent;
  private final EventProducer<FileSubmitted> eventProducer;

  @SneakyThrows
  public FileSubmission create(MultipartFile multipartFile, String email) {
    var original = ImageIO.read(multipartFile.getInputStream());
    var gray =
        new BufferedImage(original.getWidth(), original.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
    var op = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
    op.filter(original, gray);

    var submissionId = UUID.randomUUID();
    var bucketKey = "file-submissions/" + submissionId + "/image.jpg";
    var tempFile = File.createTempFile("upload-", ".jpg");
    ImageIO.write(gray, "jpg", tempFile);
    bucketComponent.upload(tempFile, bucketKey);
    tempFile.delete();

    var submission =
        FileSubmission.builder()
            .id(submissionId)
            .fileKey(bucketKey)
            .fileName(multipartFile.getOriginalFilename())
            .email(email)
            .createdAt(Instant.now())
            .build();

    var saved = mapper.toModel(repository.save(mapper.toEntity(submission)));

    eventProducer.accept(List.of(new FileSubmitted(saved.id())));
    return saved;
  }

  public List<FileSubmission> getAll() {
    return repository.findAll().stream().map(mapper::toModel).toList();
  }
}

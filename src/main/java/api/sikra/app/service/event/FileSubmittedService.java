package api.sikra.app.service.event;

import api.sikra.app.endpoint.event.model.FileSubmitted;
import api.sikra.app.file.bucket.BucketComponent;
import api.sikra.app.mail.Email;
import api.sikra.app.mail.Mailer;
import api.sikra.app.repository.FileSubmissionRepository;
import api.sikra.app.repository.UserRepository;
import jakarta.mail.internet.InternetAddress;
import jakarta.persistence.EntityNotFoundException;
import java.time.Duration;
import java.util.List;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class FileSubmittedService implements Consumer<FileSubmitted> {

  private final FileSubmissionRepository repository;
  private final UserRepository userRepository;
  private final BucketComponent bucketComponent;
  private final Mailer mailer;
  private final EmailTemplateService emailTemplateService;

  @Override
  @Transactional(readOnly = true)
  @SneakyThrows
  public void accept(FileSubmitted event) {
    var entity =
        repository
            .findById(event.getSubmissionId())
            .orElseThrow(
                () -> new EntityNotFoundException(
                    "Submission not found: " + event.getSubmissionId()));

    var userEmail = resolveUserEmail(entity.getUser().getId());
    var downloadUrl = bucketComponent.presign(entity.getFileKey(), Duration.ofDays(7));

    var subject = "File submitted successfully - " + entity.getFileName();
    var htmlBody =
        emailTemplateService.renderFileSubmissionConfirmation(
            entity.getFileName(), downloadUrl.toString());

    mailer.accept(
        new Email(
            new InternetAddress(userEmail),
            List.of(),
            List.of(),
            subject,
            htmlBody,
            List.of()));
  }

  private String resolveUserEmail(UUID userId) {
    return userRepository
        .findById(userId)
        .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId))
        .getEmail();
  }
}

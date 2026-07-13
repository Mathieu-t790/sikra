package api.sikra.app.service.event;

import api.sikra.app.endpoint.event.model.FileSubmitted;
import api.sikra.app.file.bucket.BucketComponent;
import api.sikra.app.mail.Email;
import api.sikra.app.mail.Mailer;
import api.sikra.app.repository.FileSubmissionRepository;
import api.sikra.app.repository.model.JFileSubmission;
import jakarta.mail.internet.InternetAddress;
import java.time.Duration;
import java.util.List;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class FileSubmittedService implements Consumer<FileSubmitted> {

  private final FileSubmissionRepository repository;
  private final BucketComponent bucketComponent;
  private final Mailer mailer;
  private final EmailTemplateService emailTemplateService;

  @Override
  @SneakyThrows
  public void accept(FileSubmitted event) {
    var entity =
        repository
            .findById(event.getSubmissionId())
            .orElseThrow(() -> new RuntimeException("Submission not found: " + event.getSubmissionId()));

    var downloadUrl = bucketComponent.presign(entity.getFileKey(), Duration.ofDays(7));

    var subject = "File submitted successfully - " + entity.getFileName();
    var htmlBody =
        emailTemplateService.renderFileSubmissionConfirmation(
            entity.getFileName(), downloadUrl.toString());

    mailer.accept(
        new Email(
            new InternetAddress(entity.getEmail()),
            List.of(),
            List.of(),
            subject,
            htmlBody,
            List.of()));
  }
}

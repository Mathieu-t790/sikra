package api.sikra.app.service.event;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import api.sikra.app.endpoint.event.model.FileSubmitted;
import api.sikra.app.file.bucket.BucketComponent;
import api.sikra.app.mail.Email;
import api.sikra.app.mail.Mailer;
import api.sikra.app.repository.FileSubmissionRepository;
import api.sikra.app.repository.model.JFileSubmission;
import java.net.URL;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FileSubmittedServiceTest {

  @Mock private FileSubmissionRepository repository;
  @Mock private BucketComponent bucketComponent;
  @Mock private Mailer mailer;
  @Mock private EmailTemplateService emailTemplateService;

  private FileSubmittedService service;

  private final UUID submissionId = UUID.randomUUID();

  @BeforeEach
  void setUp() {
    service =
        new FileSubmittedService(repository, bucketComponent, mailer, emailTemplateService);
  }

  @Test
  void accept_should_send_email_when_submission_exists() throws Exception {
    var entity = new JFileSubmission();
    entity.setId(submissionId);
    entity.setFileKey("file-submissions/" + submissionId + "/image.jpg");
    entity.setFileName("test.jpg");
    entity.setEmail("test@example.com");

    var downloadUrl = new URL("https://bucket.example.com/key");

    when(repository.findById(submissionId)).thenReturn(Optional.of(entity));
    when(bucketComponent.presign(entity.getFileKey(), Duration.ofDays(7)))
        .thenReturn(downloadUrl);
    when(emailTemplateService.renderFileSubmissionConfirmation("test.jpg", downloadUrl.toString()))
        .thenReturn("<html><body>Email body</body></html>");

    service.accept(new FileSubmitted(submissionId));

    verify(mailer).accept(any(Email.class));
  }

  @Test
  void accept_should_throw_when_submission_not_found() {
    when(repository.findById(submissionId)).thenReturn(Optional.empty());

    assertThrows(RuntimeException.class, () -> service.accept(new FileSubmitted(submissionId)));
  }
}

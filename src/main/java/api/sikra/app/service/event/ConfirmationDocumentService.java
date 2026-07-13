package api.sikra.app.service.event;

import api.sikra.app.file.bucket.BucketComponent;
import api.sikra.app.model.Course;
import api.sikra.app.model.User;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.time.Duration;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ConfirmationDocumentService {

  private final PdfGenerationService pdfGenerationService;
  private final BucketComponent bucketComponent;

  @SneakyThrows
  public URL generateAndUpload(UUID subscriptionId, User user, Course course) {
    var fullName = user.firstName() + " " + user.lastName();
    var pdfBytes =
        pdfGenerationService.generateSubscriptionConfirmation(
            fullName, user.userName(), course.title());

    var bucketKey = "subscriptions/" + subscriptionId + "/confirmation.pdf";
    var tempFile = File.createTempFile("confirmation-", ".pdf");
    try (var fos = new FileOutputStream(tempFile)) {
      fos.write(pdfBytes);
    }

    bucketComponent.upload(tempFile, bucketKey);
    tempFile.delete();
    return bucketComponent.presign(bucketKey, Duration.ofDays(7));
  }
}

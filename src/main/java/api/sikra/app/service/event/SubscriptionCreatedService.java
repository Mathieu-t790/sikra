package api.sikra.app.service.event;

import api.sikra.app.endpoint.event.model.SubscriptionCreated;
import api.sikra.app.mail.Email;
import api.sikra.app.mail.Mailer;
import api.sikra.app.model.Course;
import api.sikra.app.model.EmailStatus;
import api.sikra.app.model.Subscription;
import api.sikra.app.model.SubscriptionStatus;
import api.sikra.app.model.User;
import api.sikra.app.repository.EmailHistoryRepository;
import api.sikra.app.repository.SubscriptionRepository;
import api.sikra.app.repository.model.JEmailHistory;
import api.sikra.app.service.CourseService;
import api.sikra.app.service.UserService;
import jakarta.mail.internet.InternetAddress;
import java.net.URL;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SubscriptionCreatedService implements Consumer<SubscriptionCreated> {

  private final Mailer mailer;
  private final UserService userService;
  private final CourseService courseService;
  private final SubscriptionRepository subscriptionRepository;
  private final EmailHistoryRepository emailHistoryRepository;
  private final EmailTemplateService emailTemplateService;
  private final ConfirmationDocumentService confirmationDocumentService;

  @Override
  @SneakyThrows
  public void accept(SubscriptionCreated event) {
    var subscription = event.getSubscription();
    var user = userService.getById(subscription.userId());
    var course = courseService.getById(subscription.courseId());
    var downloadUrl =
        confirmationDocumentService.generateAndUpload(subscription.id(), user, course);
    sendEmail(subscription, user, course, downloadUrl);
  }

  @SneakyThrows
  private void sendEmail(Subscription subscription, User user, Course course, URL downloadUrl) {
    var to = user.email();
    var subject = "Subscription confirmation - " + course.title();
    var htmlBody =
        emailTemplateService.renderSubscriptionConfirmation(
            user.userName(), course.title(), downloadUrl.toString());
    try {
      mailer.accept(
          new Email(new InternetAddress(to), List.of(), List.of(), subject, htmlBody, List.of()));
      saveEmailHistory(subscription.id(), to, subject, EmailStatus.SENT, null);
      updateSubscriptionStatus(subscription.id(), SubscriptionStatus.ACTIVE);
    } catch (Exception e) {
      saveEmailHistory(subscription.id(), to, subject, EmailStatus.FAILED, e.getMessage());
      updateSubscriptionStatus(subscription.id(), SubscriptionStatus.FAILED);
    }
  }

  private void saveEmailHistory(
      UUID subscriptionId, String recipient, String subject, EmailStatus status, String error) {
    emailHistoryRepository.save(
        JEmailHistory.builder()
            .id(UUID.randomUUID())
            .subscriptionId(subscriptionId)
            .recipient(recipient)
            .subject(subject)
            .status(status)
            .errorMessage(error)
            .sentAt(Instant.now())
            .build());
  }

  private void updateSubscriptionStatus(UUID subscriptionId, SubscriptionStatus status) {
    subscriptionRepository
        .findById(subscriptionId)
        .ifPresent(
            entity -> {
              entity.setStatus(status);
              subscriptionRepository.save(entity);
            });
  }
}

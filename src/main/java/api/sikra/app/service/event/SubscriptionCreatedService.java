package api.sikra.app.service.event;

import api.sikra.app.endpoint.event.model.SubscriptionCreated;
import api.sikra.app.mail.Email;
import api.sikra.app.mail.Mailer;
import api.sikra.app.model.EmailStatus;
import api.sikra.app.model.SubscriptionStatus;
import api.sikra.app.repository.EmailHistoryRepository;
import api.sikra.app.repository.SubscriptionRepository;
import api.sikra.app.repository.UserRepository;
import api.sikra.app.repository.model.JEmailHistory;
import api.sikra.app.repository.model.JUser;
import jakarta.mail.internet.InternetAddress;
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
  private final UserRepository userRepository;
  private final SubscriptionRepository subscriptionRepository;
  private final EmailHistoryRepository emailHistoryRepository;

  @Override
  @SneakyThrows
  public void accept(SubscriptionCreated event) {
    var subscription = event.getSubscription();
    var userId = subscription.userId();
    var courseId = subscription.courseId();

    var userEntity =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found for subscription"));

    var to = userEntity.getEmail();
    var subject = "Subscription confirmation";
    var htmlBody = buildEmailBody(userEntity);

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

  private String buildEmailBody(JUser user) {
    return """
           <html>
             <body>
               <p>Dear %s,</p>
               <p>Your subscription has been confirmed.</p>
               <p>Thank you for joining us!</p>
               <p>Best regards,</p>
               <p>The Team</p>
             </body>
           </html>
           """
        .formatted(user.getUserName());
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

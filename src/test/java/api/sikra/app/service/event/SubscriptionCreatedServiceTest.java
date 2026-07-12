package api.sikra.app.service.event;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import api.sikra.app.endpoint.event.model.SubscriptionCreated;
import api.sikra.app.mail.Email;
import api.sikra.app.mail.Mailer;
import api.sikra.app.model.EmailStatus;
import api.sikra.app.model.Subscription;
import api.sikra.app.model.SubscriptionStatus;
import api.sikra.app.repository.EmailHistoryRepository;
import api.sikra.app.repository.SubscriptionRepository;
import api.sikra.app.repository.UserRepository;
import api.sikra.app.repository.model.JEmailHistory;
import api.sikra.app.repository.model.JSubscription;
import api.sikra.app.repository.model.JUser;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SubscriptionCreatedServiceTest {

  @Mock private Mailer mailer;
  @Mock private UserRepository userRepository;
  @Mock private SubscriptionRepository subscriptionRepository;
  @Mock private EmailHistoryRepository emailHistoryRepository;
  @Captor private ArgumentCaptor<JEmailHistory> emailHistoryCaptor;

  private SubscriptionCreatedService service;

  private final UUID userId = UUID.randomUUID();
  private final UUID courseId = UUID.randomUUID();
  private final UUID subscriptionId = UUID.randomUUID();

  @BeforeEach
  void setUp() {
    service =
        new SubscriptionCreatedService(
            mailer, userRepository, subscriptionRepository, emailHistoryRepository);
  }

  @Test
  void should_send_email_and_update_status_when_mailer_succeeds() {
    var subscription =
        Subscription.builder()
            .id(subscriptionId)
            .userId(userId)
            .courseId(courseId)
            .status(SubscriptionStatus.PENDING)
            .createdAt(Instant.now())
            .build();

    var userEntity = new JUser();
    userEntity.setId(userId);
    userEntity.setUserName("mata");
    userEntity.setEmail("hei.tafita.2@gmail.com");

    var jSubscription = new JSubscription();
    jSubscription.setId(subscriptionId);
    jSubscription.setStatus(SubscriptionStatus.PENDING);

    when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
    when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.of(jSubscription));

    service.accept(new SubscriptionCreated(subscription));

    verify(mailer).accept(any(Email.class));
    verify(emailHistoryRepository).save(emailHistoryCaptor.capture());
    verify(subscriptionRepository).save(jSubscription);

    assertEquals(EmailStatus.SENT, emailHistoryCaptor.getValue().getStatus());
    assertEquals("hei.tafita.2@gmail.com", emailHistoryCaptor.getValue().getRecipient());
    assertEquals(SubscriptionStatus.ACTIVE, jSubscription.getStatus());
  }

  @Test
  void should_save_failed_status_when_mailer_throws() {
    var subscription =
        Subscription.builder()
            .id(subscriptionId)
            .userId(userId)
            .courseId(courseId)
            .status(SubscriptionStatus.PENDING)
            .createdAt(Instant.now())
            .build();

    var userEntity = new JUser();
    userEntity.setId(userId);
    userEntity.setUserName("mata");
    userEntity.setEmail("hei.tafita.2@gmail.com");

    var jSubscription = new JSubscription();
    jSubscription.setId(subscriptionId);
    jSubscription.setStatus(SubscriptionStatus.PENDING);

    when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
    when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.of(jSubscription));
    doThrow(new RuntimeException("SES error")).when(mailer).accept(any(Email.class));

    service.accept(new SubscriptionCreated(subscription));

    verify(emailHistoryRepository).save(emailHistoryCaptor.capture());
    verify(subscriptionRepository).save(jSubscription);

    assertEquals(EmailStatus.FAILED, emailHistoryCaptor.getValue().getStatus());
    assertEquals("SES error", emailHistoryCaptor.getValue().getErrorMessage());
    assertEquals(SubscriptionStatus.FAILED, jSubscription.getStatus());
  }
}

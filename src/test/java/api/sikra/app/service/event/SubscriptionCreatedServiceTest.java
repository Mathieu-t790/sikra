package api.sikra.app.service.event;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import api.sikra.app.repository.model.JSubscription;
import api.sikra.app.service.CourseService;
import api.sikra.app.service.UserService;
import java.net.URL;
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
  @Mock private UserService userService;
  @Mock private CourseService courseService;
  @Mock private SubscriptionRepository subscriptionRepository;
  @Mock private EmailHistoryRepository emailHistoryRepository;
  @Mock private EmailTemplateService emailTemplateService;
  @Mock private ConfirmationDocumentService confirmationDocumentService;
  @Captor private ArgumentCaptor<JEmailHistory> emailHistoryCaptor;

  private SubscriptionCreatedService service;

  private final UUID userId = UUID.randomUUID();
  private final UUID courseId = UUID.randomUUID();
  private final UUID subscriptionId = UUID.randomUUID();
  private final URL fakeDownloadUrl = new URL("https://bucket.example.com/key");

  SubscriptionCreatedServiceTest() throws Exception {}

  @BeforeEach
  void setUp() {
    service =
        new SubscriptionCreatedService(
            mailer,
            userService,
            courseService,
            subscriptionRepository,
            emailHistoryRepository,
            emailTemplateService,
            confirmationDocumentService);
  }

  @Test
  void should_send_email_and_update_status_when_mailer_succeeds() throws Exception {
    var subscription =
        Subscription.builder()
            .id(subscriptionId)
            .userId(userId)
            .courseId(courseId)
            .status(SubscriptionStatus.PENDING)
            .createdAt(Instant.now())
            .build();

    var user =
        User.builder()
            .id(userId)
            .firstName("Mathieu")
            .lastName("RAZAFIMANDIMBY")
            .userName("mata")
            .email("hei.tafita.2@gmail.com")
            .build();

    var course = Course.builder().id(courseId).title("Prog4").build();

    var jSubscription = new JSubscription();
    jSubscription.setId(subscriptionId);
    jSubscription.setStatus(SubscriptionStatus.PENDING);

    when(userService.getById(userId)).thenReturn(user);
    when(courseService.getById(courseId)).thenReturn(course);
    when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.of(jSubscription));
    when(confirmationDocumentService.generateAndUpload(eq(subscriptionId), eq(user), eq(course)))
        .thenReturn(fakeDownloadUrl);
    when(emailTemplateService.renderSubscriptionConfirmation(eq("mata"), eq("Prog4"), anyString()))
        .thenReturn("<html><body>Styled email</body></html>");

    service.accept(new SubscriptionCreated(subscription));

    verify(mailer).accept(any(Email.class));
    verify(emailHistoryRepository).save(emailHistoryCaptor.capture());
    verify(subscriptionRepository).save(jSubscription);

    assertEquals(EmailStatus.SENT, emailHistoryCaptor.getValue().getStatus());
    assertEquals("hei.tafita.2@gmail.com", emailHistoryCaptor.getValue().getRecipient());
    assertEquals(SubscriptionStatus.ACTIVE, jSubscription.getStatus());
  }

  @Test
  void should_save_failed_status_when_mailer_throws() throws Exception {
    var subscription =
        Subscription.builder()
            .id(subscriptionId)
            .userId(userId)
            .courseId(courseId)
            .status(SubscriptionStatus.PENDING)
            .createdAt(Instant.now())
            .build();

    var user =
        User.builder()
            .id(userId)
            .firstName("Mathieu")
            .lastName("RAZAFIMANDIMBY")
            .userName("mata")
            .email("hei.tafita.2@gmail.com")
            .build();

    var course = Course.builder().id(courseId).title("Prog4").build();

    var jSubscription = new JSubscription();
    jSubscription.setId(subscriptionId);
    jSubscription.setStatus(SubscriptionStatus.PENDING);

    when(userService.getById(userId)).thenReturn(user);
    when(courseService.getById(courseId)).thenReturn(course);
    when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.of(jSubscription));
    when(confirmationDocumentService.generateAndUpload(eq(subscriptionId), eq(user), eq(course)))
        .thenReturn(fakeDownloadUrl);
    when(emailTemplateService.renderSubscriptionConfirmation(eq("mata"), eq("Prog4"), anyString()))
        .thenReturn("<html><body>Styled email</body></html>");
    doThrow(new RuntimeException("SES error")).when(mailer).accept(any(Email.class));

    service.accept(new SubscriptionCreated(subscription));

    verify(emailHistoryRepository).save(emailHistoryCaptor.capture());
    verify(subscriptionRepository).save(jSubscription);

    assertEquals(EmailStatus.FAILED, emailHistoryCaptor.getValue().getStatus());
    assertEquals("SES error", emailHistoryCaptor.getValue().getErrorMessage());
    assertEquals(SubscriptionStatus.FAILED, jSubscription.getStatus());
  }
}

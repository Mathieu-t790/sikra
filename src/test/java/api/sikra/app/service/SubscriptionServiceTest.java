package api.sikra.app.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import api.sikra.app.endpoint.event.EventProducer;
import api.sikra.app.endpoint.event.model.SubscriptionCreated;
import api.sikra.app.mapper.SubscriptionMapper;
import api.sikra.app.model.Subscription;
import api.sikra.app.model.SubscriptionStatus;
import api.sikra.app.repository.CourseRepository;
import api.sikra.app.repository.SubscriptionRepository;
import api.sikra.app.repository.UserRepository;
import api.sikra.app.repository.model.JCourse;
import api.sikra.app.repository.model.JSubscription;
import api.sikra.app.repository.model.JUser;
import jakarta.persistence.EntityNotFoundException;
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
class SubscriptionServiceTest {

  @Mock private UserRepository userRepository;
  @Mock private CourseRepository courseRepository;
  @Mock private SubscriptionRepository subscriptionRepository;
  @Mock private SubscriptionMapper mapper;
  @Mock private EventProducer<SubscriptionCreated> eventProducer;
  @Captor private ArgumentCaptor<java.util.List<SubscriptionCreated>> eventCaptor;

  private SubscriptionService service;

  private final UUID userId = UUID.randomUUID();
  private final UUID courseId = UUID.randomUUID();

  @BeforeEach
  void setUp() {
    service =
        new SubscriptionService(
            userRepository, courseRepository, subscriptionRepository, mapper, eventProducer);
  }

  @Test
  void create_should_succeed_when_valid() {
    var userEntity = new JUser();
    userEntity.setId(userId);
    userEntity.setUserName("mata");
    userEntity.setEmail("mata@cu.te");

    var courseEntity = new JCourse();
    courseEntity.setId(courseId);
    courseEntity.setTitle("Prog4");

    var jSubscriptionSaved = new JSubscription();
    jSubscriptionSaved.setId(UUID.randomUUID());
    jSubscriptionSaved.setStatus(SubscriptionStatus.PENDING);
    jSubscriptionSaved.setCourse(courseEntity);
    jSubscriptionSaved.setUser(userEntity);

    var expectedModel =
        Subscription.builder()
            .id(jSubscriptionSaved.getId())
            .status(SubscriptionStatus.PENDING)
            .courseId(courseId)
            .userId(userId)
            .build();

    when(subscriptionRepository.findByUser_IdAndCourse_Id(userId, courseId))
        .thenReturn(Optional.empty());
    when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
    when(courseRepository.findById(courseId)).thenReturn(Optional.of(courseEntity));
    when(mapper.toNewEntity(eq(userEntity), eq(courseEntity), any(Subscription.class)))
        .thenReturn(jSubscriptionSaved);
    when(subscriptionRepository.save(jSubscriptionSaved)).thenReturn(jSubscriptionSaved);
    when(mapper.toModel(jSubscriptionSaved)).thenReturn(expectedModel);

    var result = service.create(courseId, userId);

    assertEquals(expectedModel, result);
    verify(eventProducer).accept(eventCaptor.capture());
    assertEquals(1, eventCaptor.getValue().size());
  }

  @Test
  void create_should_throw_when_duplicate() {
    var existing = new JSubscription();
    when(subscriptionRepository.findByUser_IdAndCourse_Id(userId, courseId))
        .thenReturn(Optional.of(existing));

    assertThrows(IllegalStateException.class, () -> service.create(courseId, userId));
  }

  @Test
  void create_should_throw_when_user_not_found() {
    when(subscriptionRepository.findByUser_IdAndCourse_Id(userId, courseId))
        .thenReturn(Optional.empty());
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> service.create(courseId, userId));
  }

  @Test
  void create_should_throw_when_course_not_found() {
    var userEntity = new JUser();
    userEntity.setId(userId);

    when(subscriptionRepository.findByUser_IdAndCourse_Id(userId, courseId))
        .thenReturn(Optional.empty());
    when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
    when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> service.create(courseId, userId));
  }
}

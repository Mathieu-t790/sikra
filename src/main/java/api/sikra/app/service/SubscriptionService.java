package api.sikra.app.service;

import static api.sikra.app.model.SubscriptionStatus.PENDING;

import jakarta.persistence.EntityNotFoundException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import api.sikra.app.endpoint.event.EventProducer;
import api.sikra.app.endpoint.event.model.SubscriptionCreated;
import api.sikra.app.mapper.SubscriptionMapper;
import api.sikra.app.model.Subscription;
import api.sikra.app.repository.CourseRepository;
import api.sikra.app.repository.SubscriptionRepository;
import api.sikra.app.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SubscriptionService {

  private final UserRepository userRepository;
  private final CourseRepository courseRepository;
  private final SubscriptionRepository subscriptionRepository;
  private final SubscriptionMapper mapper;
  private final EventProducer<SubscriptionCreated> eventProducer;

  public Subscription create(UUID courseId, UUID userId) {
    subscriptionRepository
        .findByUser_IdAndCourse_Id(userId, courseId)
        .ifPresent(s -> { throw new IllegalStateException("Already subscribed"); });

    var userEntity = userRepository
        .findById(userId)
        .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));
    var courseEntity = courseRepository
        .findById(courseId)
        .orElseThrow(() -> new EntityNotFoundException("Course not found: " + courseId));

    var subscription = Subscription.builder()
        .id(UUID.randomUUID())
        .createdAt(Instant.now())
        .status(PENDING)
        .courseId(courseId)
        .userId(userId)
        .build();

    var entity = mapper.toNewEntity(userEntity, courseEntity, subscription);
    var saved = mapper.toModel(subscriptionRepository.save(entity));

    eventProducer.accept(List.of(new SubscriptionCreated(saved)));
    return saved;
  }
}

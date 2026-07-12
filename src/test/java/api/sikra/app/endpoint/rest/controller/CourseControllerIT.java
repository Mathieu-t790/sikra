package api.sikra.app.endpoint.rest.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

import api.sikra.app.conf.FacadeIT;
import api.sikra.app.endpoint.event.EventProducer;
import api.sikra.app.endpoint.event.model.SubscriptionCreated;
import api.sikra.app.endpoint.rest.controller.dto.SubscriptionRequest;
import api.sikra.app.endpoint.rest.controller.dto.SubscriptionResponse;
import api.sikra.app.model.SubscriptionStatus;
import api.sikra.app.repository.CourseRepository;
import api.sikra.app.repository.SubscriptionRepository;
import api.sikra.app.repository.UserRepository;
import api.sikra.app.repository.model.JCourse;
import api.sikra.app.repository.model.JUser;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class CourseControllerIT extends FacadeIT {

  @Autowired private TestRestTemplate restTemplate;
  @Autowired private UserRepository userRepository;
  @Autowired private CourseRepository courseRepository;
  @Autowired private SubscriptionRepository subscriptionRepository;

  @MockBean private EventProducer<SubscriptionCreated> eventProducer;

  private UUID userId;
  private UUID courseId;

  @BeforeEach
  void setUp() {
    doNothing().when(eventProducer).accept(any());

    subscriptionRepository.deleteAll();
    userRepository.deleteAll();
    courseRepository.deleteAll();

    var user = new JUser();
    user.setId(UUID.randomUUID());
    user.setUserName("mata");
    user.setLastName("Mata");
    user.setEmail("mata@cu.te");
    userId = userRepository.save(user).getId();

    var course = new JCourse();
    course.setId(UUID.randomUUID());
    course.setTitle("Prog4");
    courseId = courseRepository.save(course).getId();
  }

  @Test
  void subscribe_should_return_created() {
    var request = new SubscriptionRequest(userId);
    var url = "/courses/" + courseId + "/subscribe";

    ResponseEntity<SubscriptionResponse> response =
        restTemplate.postForEntity(url, request, SubscriptionResponse.class);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(SubscriptionStatus.PENDING, response.getBody().status());
    assertNotNull(response.getBody().subscriptionId());
  }

  @Test
  void subscribe_should_return_409_when_duplicate() {
    var request = new SubscriptionRequest(userId);
    var url = "/courses/" + courseId + "/subscribe";

    restTemplate.postForEntity(url, request, SubscriptionResponse.class);
    ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

    assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
  }

  @Test
  void subscribe_should_return_404_when_user_not_found() {
    var request = new SubscriptionRequest(UUID.randomUUID());
    var url = "/courses/" + courseId + "/subscribe";

    ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  void subscribe_should_return_404_when_course_not_found() {
    var request = new SubscriptionRequest(userId);
    var url = "/courses/" + UUID.randomUUID() + "/subscribe";

    ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }
}

package api.sikra.app.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import api.sikra.app.mapper.CourseMapper;
import api.sikra.app.model.Course;
import api.sikra.app.repository.CourseRepository;
import api.sikra.app.repository.model.JCourse;
import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

  @Mock private CourseMapper mapper;
  @Mock private CourseRepository repository;

  private CourseService service;
  private final UUID courseId = UUID.randomUUID();

  @BeforeEach
  void setUp() {
    service = new CourseService(mapper, repository);
  }

  @Test
  void getById_should_return_course() {
    var entity = new JCourse();
    entity.setId(courseId);
    entity.setTitle("Prog4");

    var course = new Course(courseId, "Prog4", null, null);

    when(repository.findById(courseId)).thenReturn(Optional.of(entity));
    when(mapper.toModel(entity)).thenReturn(course);

    var result = service.getById(courseId);

    assertEquals(course, result);
  }

  @Test
  void getById_should_throw_when_not_found() {
    when(repository.findById(courseId)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> service.getById(courseId));
  }
}

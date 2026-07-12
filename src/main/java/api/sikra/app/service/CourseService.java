package api.sikra.app.service;

import jakarta.persistence.EntityNotFoundException;
import java.util.UUID;
import lombok.AllArgsConstructor;
import api.sikra.app.mapper.CourseMapper;
import api.sikra.app.model.Course;
import api.sikra.app.repository.CourseRepository;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CourseService {

  private final CourseMapper mapper;
  private final CourseRepository repository;

  public Course getById(UUID id) {
    return mapper.toModel(
        repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Course not found: " + id)));
  }
}

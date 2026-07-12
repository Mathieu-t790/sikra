package api.sikra.app.mapper;

import api.sikra.app.model.Course;
import api.sikra.app.repository.model.JCourse;
import org.springframework.stereotype.Component;

@Component
public class CourseMapper {

  public Course toModel(JCourse entity) {
    return Course.builder()
        .id(entity.getId())
        .title(entity.getTitle())
        .start(entity.getStart())
        .end(entity.getEnd())
        .build();
  }

  public JCourse toEntity(Course model) {
    return JCourse.builder()
        .id(model.id())
        .title(model.title())
        .start(model.start())
        .end(model.end())
        .build();
  }
}

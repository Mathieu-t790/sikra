package api.sikra.app.mapper;

import api.sikra.app.model.Student;
import api.sikra.app.repository.model.JStudent;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class StudentMapper {

  private final UserMapper userMapper;

  public Student toModel(JStudent entity) {
    return Student.builder()
        .id(entity.getId())
        .user(userMapper.toModel(entity.getUser()))
        .reference(entity.getReference())
        .level(entity.getLevel())
        .build();
  }

  public JStudent toEntity(Student model) {
    return JStudent.builder()
        .id(model.id())
        .user(userMapper.toEntity(model.user()))
        .reference(model.reference())
        .level(model.level())
        .build();
  }
}

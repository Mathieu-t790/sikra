package api.sikra.app.mapper;

import api.sikra.app.model.User;
import api.sikra.app.repository.model.JUser;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

  public User toModel(JUser entity) {
    return User.builder()
        .id(entity.getId())
        .firstName(entity.getFirstName())
        .lastName(entity.getLastName())
        .userName(entity.getUserName())
        .email(entity.getEmail())
        .build();
  }

  public JUser toEntity(User model) {
    return JUser.builder()
        .id(model.id())
        .firstName(model.firstName())
        .lastName(model.lastName())
        .userName(model.userName())
        .email(model.email())
        .build();
  }
}

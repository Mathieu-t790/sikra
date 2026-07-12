package api.sikra.app.mapper;

import org.springframework.stereotype.Component;
import api.sikra.app.model.Subscription;
import api.sikra.app.repository.model.JCourse;
import api.sikra.app.repository.model.JSubscription;
import api.sikra.app.repository.model.JUser;

@Component
public class SubscriptionMapper {

  public Subscription toModel(JSubscription entity) {
    return Subscription.builder()
        .id(entity.getId())
        .createdAt(entity.getCreatedAt())
        .status(entity.getStatus())
        .courseId(entity.getCourse().getId())
        .userId(entity.getUser().getId())
        .build();
  }

  public JSubscription toNewEntity(JUser userEntity, JCourse courseEntity, Subscription model) {
    return JSubscription.builder()
        .id(model.id())
        .createdAt(model.createdAt())
        .status(model.status())
        .course(courseEntity)
        .user(userEntity)
        .build();
  }
}

package api.sikra.app.repository;

import api.sikra.app.repository.model.JSubscription;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionRepository extends JpaRepository<JSubscription, UUID> {
  Optional<JSubscription> findByUser_IdAndCourse_Id(UUID userId, UUID courseId);
}

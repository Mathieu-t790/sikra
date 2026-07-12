package api.sikra.app.repository.model;

import static jakarta.persistence.EnumType.STRING;

import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import api.sikra.app.model.SubscriptionStatus;
import org.hibernate.annotations.CreationTimestamp;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "subscription")
public class JSubscription {
  @Id @GeneratedValue private UUID id;

  @CreationTimestamp private Instant createdAt;

  @Enumerated(STRING)
  private SubscriptionStatus status;

  @ManyToOne
  @JoinColumn(name = "course_id")
  private JCourse course;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private JUser user;
}

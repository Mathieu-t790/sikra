package api.sikra.app.repository.model;

import static jakarta.persistence.EnumType.STRING;

import api.sikra.app.model.SubscriptionStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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

  @CreationTimestamp
  @Column(nullable = false, updatable = false)
  private Instant createdAt;

  @NotNull
  @Enumerated(STRING)
  @Column(nullable = false)
  private SubscriptionStatus status;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "course_id", nullable = false)
  private JCourse course;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private JUser user;
}

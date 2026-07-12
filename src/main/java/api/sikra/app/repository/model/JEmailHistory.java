package api.sikra.app.repository.model;

import static jakarta.persistence.EnumType.STRING;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import api.sikra.app.model.EmailStatus;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
@Table(name = "email_history")
public class JEmailHistory {
  @Id @GeneratedValue private UUID id;

  @Column(name = "subscription_id")
  private UUID subscriptionId;

  private String recipient;

  private String subject;

  @Enumerated(STRING)
  private EmailStatus status;

  @Column(columnDefinition = "text")
  private String errorMessage;

  private Instant sentAt;
}

package api.sikra.app.repository.model;

import static jakarta.persistence.EnumType.STRING;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

  @NotNull
  @Column(name = "subscription_id", nullable = false)
  private UUID subscriptionId;

  @NotBlank
  @Column(nullable = false)
  private String recipient;

  @NotBlank
  @Column(nullable = false)
  private String subject;

  @NotNull
  @Enumerated(STRING)
  @Column(nullable = false)
  private EmailStatus status;

  @Column(columnDefinition = "text")
  private String errorMessage;

  @NotNull
  @Column(nullable = false)
  private Instant sentAt;
}

package api.sikra.app.repository.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
import org.hibernate.annotations.CreationTimestamp;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "file_submission")
public class JFileSubmission {
  @Id @GeneratedValue private UUID id;

  @NotNull
  @Column(nullable = false, length = 500)
  private String fileKey;

  @NotNull
  @Column(nullable = false, length = 500)
  private String fileName;

  @NotBlank
  @Column(nullable = false)
  private String email;

  @CreationTimestamp
  @Column(nullable = false, updatable = false)
  private Instant createdAt;
}

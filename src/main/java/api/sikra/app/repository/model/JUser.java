package api.sikra.app.repository.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
@Table(name = "\"user\"")
public class JUser {
  @Id @GeneratedValue private UUID id;

  @Column(length = 200)
  private String firstName;

  @NotBlank
  @Column(nullable = false, length = 200)
  private String lastName;

  @NotBlank
  @Column(nullable = false, length = 50, unique = true)
  private String userName;

  @NotBlank
  @Column(nullable = false, unique = true)
  private String email;
}

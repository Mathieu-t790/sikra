package api.sikra.app.repository.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
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
@Table(name = "student")
public class JStudent {
  @Id @GeneratedValue private UUID id;

  @OneToOne
  @JoinColumn(name = "user_id")
  private JUser user;

  @Column(nullable = false, unique = true, length = 50)
  private String reference;

  @Column(length = 10)
  private String level;
}

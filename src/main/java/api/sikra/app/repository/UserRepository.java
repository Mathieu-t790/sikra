package api.sikra.app.repository;

import java.util.UUID;
import api.sikra.app.repository.model.JUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<JUser, UUID> {}

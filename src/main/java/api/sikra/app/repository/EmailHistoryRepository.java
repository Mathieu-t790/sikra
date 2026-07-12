package api.sikra.app.repository;

import api.sikra.app.repository.model.JEmailHistory;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailHistoryRepository extends JpaRepository<JEmailHistory, UUID> {}

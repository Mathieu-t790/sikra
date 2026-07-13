package api.sikra.app.repository;

import api.sikra.app.repository.model.JFileSubmission;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileSubmissionRepository extends JpaRepository<JFileSubmission, UUID> {}

package api.sikra.app.service;

import api.sikra.app.mapper.UserMapper;
import api.sikra.app.model.User;
import api.sikra.app.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {

  private final UserMapper mapper;
  private final UserRepository repository;

  public User getById(UUID id) {
    return mapper.toModel(
        repository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("User not found: " + id)));
  }
}

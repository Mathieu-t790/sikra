package api.sikra.app.mapper;

import api.sikra.app.model.EmailHistory;
import api.sikra.app.repository.model.JEmailHistory;
import org.springframework.stereotype.Component;

@Component
public class EmailHistoryMapper {

  public EmailHistory toModel(JEmailHistory entity) {
    return EmailHistory.builder()
        .id(entity.getId())
        .subscriptionId(entity.getSubscriptionId())
        .recipient(entity.getRecipient())
        .subject(entity.getSubject())
        .status(entity.getStatus())
        .errorMessage(entity.getErrorMessage())
        .sentAt(entity.getSentAt())
        .build();
  }

  public JEmailHistory toEntity(EmailHistory model) {
    return JEmailHistory.builder()
        .id(model.id())
        .subscriptionId(model.subscriptionId())
        .recipient(model.recipient())
        .subject(model.subject())
        .status(model.status())
        .errorMessage(model.errorMessage())
        .sentAt(model.sentAt())
        .build();
  }
}

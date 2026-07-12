package api.sikra.app.endpoint.rest.controller.dto;

import java.util.UUID;
import lombok.Builder;
import api.sikra.app.model.Subscription;
import api.sikra.app.model.SubscriptionStatus;

@Builder
public record SubscriptionResponse(UUID subscriptionId, SubscriptionStatus status, String message) {

  public static SubscriptionResponse from(Subscription subscription) {
    return SubscriptionResponse.builder()
        .subscriptionId(subscription.id())
        .status(subscription.status())
        .message("Subscription in progress, confirmation email sent")
        .build();
  }
}

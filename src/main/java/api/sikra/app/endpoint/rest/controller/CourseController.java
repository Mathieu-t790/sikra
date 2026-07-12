package api.sikra.app.endpoint.rest.controller;

import java.util.UUID;
import lombok.AllArgsConstructor;
import api.sikra.app.endpoint.rest.controller.dto.SubscriptionRequest;
import api.sikra.app.endpoint.rest.controller.dto.SubscriptionResponse;
import api.sikra.app.service.SubscriptionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/courses")
@AllArgsConstructor
public class CourseController {

  private final SubscriptionService subscriptionService;

  @PostMapping("/{id}/subscribe")
  @ResponseStatus(HttpStatus.CREATED)
  public SubscriptionResponse subscribe(
      @PathVariable UUID id, @RequestBody SubscriptionRequest request) {

    var subscription = subscriptionService.create(id, request.userId());
    return SubscriptionResponse.from(subscription);
  }
}

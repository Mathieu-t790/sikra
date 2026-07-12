package api.sikra.app.service.event;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@AllArgsConstructor
public class EmailTemplateService {

  private final TemplateEngine templateEngine;

  public String renderSubscriptionConfirmation(String userName, String courseTitle) {
    var thymeleafContext = new Context();
    thymeleafContext.setVariable("userName", userName);
    thymeleafContext.setVariable("courseTitle", courseTitle);
    thymeleafContext.setVariable(
        "date",
        Instant.now()
            .atZone(ZoneId.of("UTC"))
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
    return templateEngine.process("email/subscription-confirmation", thymeleafContext);
  }
}

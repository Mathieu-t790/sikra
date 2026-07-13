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

  public String renderFileSubmissionConfirmation(String fileName, String downloadUrl) {
    var ctx = new Context();
    ctx.setVariable("fileName", fileName);
    ctx.setVariable("downloadUrl", downloadUrl);
    ctx.setVariable(
        "date",
        Instant.now()
            .atZone(ZoneId.of("UTC"))
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
    return templateEngine.process("email/file-submission-confirmation", ctx);
  }
}

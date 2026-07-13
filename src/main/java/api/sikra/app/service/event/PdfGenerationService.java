package api.sikra.app.service.event;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import java.io.ByteArrayOutputStream;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@AllArgsConstructor
public class PdfGenerationService {

  private final TemplateEngine templateEngine;

  @SneakyThrows
  public byte[] generateSubscriptionConfirmation(
      String fullName, String userName, String courseTitle) {
    var thymeleafContext = new Context();
    thymeleafContext.setVariable("fullName", fullName);
    thymeleafContext.setVariable("userName", userName);
    thymeleafContext.setVariable("courseTitle", courseTitle);
    thymeleafContext.setVariable(
        "date",
        Instant.now()
            .atZone(ZoneId.of("UTC"))
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
    var html = templateEngine.process("pdf/subscription-confirmation", thymeleafContext);
    var os = new ByteArrayOutputStream();
    PdfRendererBuilder builder = new PdfRendererBuilder();
    builder.withHtmlContent(html, null);
    builder.toStream(os);
    builder.run();
    return os.toByteArray();
  }
}

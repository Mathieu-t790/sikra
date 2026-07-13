package api.sikra.app.endpoint.rest.controller;

import api.sikra.app.endpoint.rest.controller.dto.FileSubmissionResponse;
import api.sikra.app.service.FileSubmissionService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/file-submissions")
@AllArgsConstructor
public class FileSubmissionController {

  private final FileSubmissionService fileSubmissionService;

  @GetMapping
  public List<FileSubmissionResponse> getAll() {
    return fileSubmissionService.getAll().stream()
        .map(FileSubmissionResponse::from)
        .toList();
  }

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  public FileSubmissionResponse submit(
      @RequestParam("file") MultipartFile file, @RequestParam("email") String email) {
    var submission = fileSubmissionService.create(file, email);
    return FileSubmissionResponse.withMessage(
        submission, "File submitted successfully, confirmation email sent");
  }
}

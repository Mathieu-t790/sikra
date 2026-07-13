package api.sikra.app.endpoint.rest.controller;

import api.sikra.app.endpoint.rest.controller.dto.FileSubmissionRequest;
import api.sikra.app.endpoint.rest.controller.dto.FileSubmissionResponse;
import api.sikra.app.service.FileSubmissionService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/file-submissions")
@AllArgsConstructor
public class FileSubmissionController {

  private final FileSubmissionService fileSubmissionService;

  @GetMapping
  public Page<FileSubmissionResponse> getAll(
      @RequestParam(defaultValue = "0") int offset,
      @RequestParam(defaultValue = "20") int limit) {
    return fileSubmissionService.getAll(offset, limit).map(FileSubmissionResponse::from);
  }

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  public FileSubmissionResponse submit(
      @RequestPart("file") MultipartFile file,
      @RequestPart("request") FileSubmissionRequest request) {
    var submission = fileSubmissionService.create(file, request.userId());
    return FileSubmissionResponse.withMessage(
        submission, "File submitted successfully, confirmation email sent");
  }
}

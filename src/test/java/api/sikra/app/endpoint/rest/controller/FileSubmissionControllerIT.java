package api.sikra.app.endpoint.rest.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import api.sikra.app.conf.FacadeIT;
import api.sikra.app.endpoint.event.EventProducer;
import api.sikra.app.endpoint.event.model.FileSubmitted;
import api.sikra.app.endpoint.rest.controller.dto.FileSubmissionResponse;
import api.sikra.app.file.bucket.BucketComponent;
import api.sikra.app.file.hash.FileHash;
import api.sikra.app.file.hash.FileHashAlgorithm;
import api.sikra.app.repository.FileSubmissionRepository;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.UUID;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

class FileSubmissionControllerIT extends FacadeIT {

  @Autowired private TestRestTemplate restTemplate;
  @Autowired private FileSubmissionRepository repository;
  @Autowired private JdbcTemplate jdbcTemplate;

  @MockBean private EventProducer<FileSubmitted> eventProducer;
  @MockBean private BucketComponent bucketComponent;

  private static final UUID USER_ID = UUID.randomUUID();

  @BeforeEach
  void setUp() {
    doNothing().when(eventProducer).accept(any());
    when(bucketComponent.upload(any(), any()))
        .thenReturn(new FileHash(FileHashAlgorithm.NONE, null));
    repository.deleteAll();
    jdbcTemplate.update("delete from \"user\"");
    jdbcTemplate.update(
        "insert into \"user\" (id, first_name, last_name, user_name, email) values (?, ?, ?, ?, ?)",
        USER_ID,
        "John",
        "Doe",
        "jdoe",
        "john@example.com");
  }

  @Test
  void post_should_return_created() throws Exception {
    var imageBytes = createMinimalJpegBytes();
    var body = createMultipartBody(imageBytes, "photo.jpg", USER_ID);

    ResponseEntity<FileSubmissionResponse> response =
        restTemplate.exchange(
            "/file-submissions", HttpMethod.POST, body, FileSubmissionResponse.class);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertNotNull(response.getBody());
    assertNotNull(response.getBody().id());
    assertEquals("photo.jpg", response.getBody().fileName());
    assertEquals("john@example.com", response.getBody().email());
    assertNotNull(response.getBody().message());
  }

  @Test
  void get_should_return_paginated_list() throws Exception {
    var imageBytes = createMinimalJpegBytes();

    var postBody = createMultipartBody(imageBytes, "test.jpg", USER_ID);
    restTemplate.exchange(
        "/file-submissions", HttpMethod.POST, postBody, FileSubmissionResponse.class);

    ResponseEntity<PageResponse> response =
        restTemplate.exchange(
            "/file-submissions?offset=0&limit=20",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<PageResponse>() {});

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertThat(response.getBody().content()).hasSize(1);
    assertEquals("test.jpg", response.getBody().content().get(0).fileName());
    assertEquals(1, response.getBody().totalElements());
  }

  private record PageResponse(List<FileSubmissionResponse> content, long totalElements) {}

  private HttpEntity<MultiValueMap<String, Object>> createMultipartBody(
      byte[] imageBytes, String fileName, UUID userId) {
    var headers = new HttpHeaders();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);

    var fileResource =
        new ByteArrayResource(imageBytes) {
          @Override
          public String getFilename() {
            return fileName;
          }
        };

    var requestJson = "{\"userId\":\"" + userId + "\"}";
    var requestHeaders = new HttpHeaders();
    requestHeaders.setContentType(MediaType.APPLICATION_JSON);
    var requestEntity = new HttpEntity<>(requestJson, requestHeaders);

    var body = new LinkedMultiValueMap<String, Object>();
    body.add("file", fileResource);
    body.add("request", requestEntity);

    return new HttpEntity<>(body, headers);
  }

  private byte[] createMinimalJpegBytes() throws Exception {
    var img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
    var baos = new ByteArrayOutputStream();
    ImageIO.write(img, "jpg", baos);
    return baos.toByteArray();
  }
}

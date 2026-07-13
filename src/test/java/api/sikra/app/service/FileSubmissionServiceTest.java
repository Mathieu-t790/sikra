package api.sikra.app.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import api.sikra.app.endpoint.event.EventProducer;
import api.sikra.app.endpoint.event.model.FileSubmitted;
import api.sikra.app.file.bucket.BucketComponent;
import api.sikra.app.mapper.FileSubmissionMapper;
import api.sikra.app.model.FileSubmission;
import api.sikra.app.repository.FileSubmissionRepository;
import api.sikra.app.repository.UserRepository;
import api.sikra.app.repository.model.JFileSubmission;
import api.sikra.app.repository.model.JUser;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class FileSubmissionServiceTest {

  @Mock private FileSubmissionRepository repository;
  @Mock private FileSubmissionMapper mapper;
  @Mock private BucketComponent bucketComponent;
  @Mock private EventProducer<FileSubmitted> eventProducer;
  @Mock private UserRepository userRepository;
  @Mock private MultipartFile multipartFile;
  @Captor private ArgumentCaptor<List<FileSubmitted>> eventCaptor;

  private FileSubmissionService service;

  private final UUID userId = UUID.randomUUID();
  private final JUser jUser = JUser.builder()
      .id(userId)
      .firstName("John")
      .lastName("Doe")
      .userName("jdoe")
      .email("john@example.com")
      .build();

  @BeforeEach
  void setUp() {
    service =
        new FileSubmissionService(repository, mapper, bucketComponent, eventProducer, userRepository);
  }

  @Test
  void create_should_succeed_and_publish_event() throws Exception {
    var submissionId = UUID.randomUUID();
    var fileName = "test.jpg";
    var now = Instant.now();

    var imageBytes = createMinimalJpegBytes();
    when(multipartFile.getInputStream()).thenReturn(new ByteArrayInputStream(imageBytes));
    when(multipartFile.getOriginalFilename()).thenReturn(fileName);
    when(userRepository.findById(userId)).thenReturn(Optional.of(jUser));

    var jEntity = new JFileSubmission();
    jEntity.setId(submissionId);
    jEntity.setFileKey("file-submissions/" + submissionId + "/image.jpg");
    jEntity.setFileName(fileName);
    jEntity.setUser(jUser);
    jEntity.setCreatedAt(now);

    var model =
        FileSubmission.builder()
            .id(submissionId)
            .fileKey("file-submissions/" + submissionId + "/image.jpg")
            .fileName(fileName)
            .userId(userId)
            .createdAt(now)
            .build();

    when(mapper.toEntity(any(FileSubmission.class), any(JUser.class))).thenReturn(jEntity);
    when(repository.save(jEntity)).thenReturn(jEntity);
    when(mapper.toModel(jEntity)).thenReturn(model);

    var result = service.create(multipartFile, userId);

    assertNotNull(result);
    assertEquals(fileName, result.fileName());
    assertEquals(userId, result.userId());
    verify(bucketComponent)
        .upload(
            any(java.io.File.class),
            argThat(key -> key.startsWith("file-submissions/") && key.endsWith("/image.jpg")));
    verify(eventProducer).accept(eventCaptor.capture());
    assertEquals(1, eventCaptor.getValue().size());
    assertEquals(submissionId, eventCaptor.getValue().get(0).getSubmissionId());
  }

  @Test
  void getAll_should_return_paginated_submissions() {
    var submissions =
        List.of(
            FileSubmission.builder()
                .id(UUID.randomUUID())
                .fileName("a.jpg")
                .userId(userId)
                .build(),
            FileSubmission.builder()
                .id(UUID.randomUUID())
                .fileName("b.jpg")
                .userId(userId)
                .build());

    var entities = List.of(new JFileSubmission(), new JFileSubmission());
    var pageable = PageRequest.of(0, 10);
    var page = new PageImpl<>(entities, pageable, 2);

    when(repository.findAll(pageable)).thenReturn(page);
    when(mapper.toModel(entities.get(0))).thenReturn(submissions.get(0));
    when(mapper.toModel(entities.get(1))).thenReturn(submissions.get(1));

    var result = service.getAll(0, 10);

    assertEquals(2, result.getTotalElements());
    assertEquals(submissions, result.getContent());
  }

  @Test
  void create_should_throw_when_user_not_found() {
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    var ex =
        org.junit.jupiter.api.Assertions.assertThrows(
            jakarta.persistence.EntityNotFoundException.class,
            () -> service.create(multipartFile, userId));

    assertEquals("User not found: " + userId, ex.getMessage());
  }

  private byte[] createMinimalJpegBytes() throws Exception {
    var img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
    var baos = new ByteArrayOutputStream();
    ImageIO.write(img, "jpg", baos);
    return baos.toByteArray();
  }
}

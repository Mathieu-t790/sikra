# Sikra — File Submission Service

Upload an image → B&W conversion → S3 storage → confirmation email with download link (7 days).

---

## API

| Method | Path | Params | Response |
|--------|------|--------|----------|
| POST | `/file-submissions` | multipart: `file` + `request` (`{"userId":"uuid"}`) | `201` |
| GET | `/file-submissions` | `?offset=0&limit=20` | `200` + paginated list |

---

## Testing guide

### 1. POST a file

Use Tokimahery's user ID: **`77099c25-85ea-4f1d-ad98-69824653ca48`**

```bash
curl -X POST "https://tuwsea43c7a7ic37l2k62ysmsq0mlxwb.lambda-url.eu-west-3.on.aws/file-submissions" \
  -F "file=@./image.jpg" \
  -F "request={\"userId\":\"77099c25-85ea-4f1d-ad98-69824653ca48\"};type=application/json"
```

Expected: `201 Created` with submission JSON.

### 2. GET the list

```bash
curl "https://tuwsea43c7a7ic37l2k62ysmsq0mlxwb.lambda-url.eu-west-3.on.aws/file-submissions?offset=0&limit=20"
```

Expected: `200 OK` with paginated list.

### 3. Check email

Sent asynchronously to **toky@mail.hei.school** with a download link (valid 7 days).

---

## Tests

```bash
./gradlew test
```

Unit (Mockito) + integration (Testcontainers) — JaCoCo min 50%.

---

## DB migrations

| File | What it does |
|------|-------------|
| `V42_3__Create_file_submission_table.sql` | `file_submission` table |
| `V42_4__Create_user_table.sql` | `"user"` table |
| `V42_5__Alter_file_submission_add_user_id.sql` | Adds `user_id` FK, drops `email` |

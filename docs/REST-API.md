# Tutor Booking REST API

Phiên b?n: 1.0.0  
Base URL m?c d?nh: `http://localhost:8080`

## Ch?ng th?c

### `POST /api/v1/auth/register`
- Body JSON:
  ```json
  {
    "email": "parent@example.com",
    "password": "Password123",
    "fullName": "Parent User",
    "phoneNumber": "0123456789",
    "role": "PARENT"
  }
  ```
- Tr? v? `201` + `AuthResponse` (JWT + thông tin tài kho?n).
- L?i ph? bi?n: `400` (sai d? li?u), `409` (email dã t?n t?i).

### `POST /api/v1/auth/login`
- Body JSON:
  ```json
  {
    "email": "parent@example.com",
    "password": "Password123"
  }
  ```
- Tr? v? `200` + `AuthResponse`.
- L?i: `401` n?u sai thông tin.

`AuthResponse` d?ng:
```json
{
  "tokenType": "Bearer",
  "accessToken": "<jwt>",
  "expiresAt": "2025-09-28T09:41:00Z",
  "user": {
    "id": "...",
    "email": "...",
    "fullName": "...",
    "phoneNumber": "...",
    "role": "PARENT",
    "status": "ACTIVE"
  }
}
```

## Booking
T?t c? endpoint du?i dây yêu c?u header `Authorization: Bearer <token>`.

`BookingResponse` m?u:
```json
{
  "id": "...",
  "studentProfileId": "...",
  "tutorProfileId": "...",
  "subjectId": "...",
  "startTime": "2025-09-30T12:00:00Z",
  "durationMinutes": 60,
  "totalFee": 450000.0,
  "status": "REQUESTED",
  "meetingLink": "https://meet.example.com/...",
  "notes": "..."
}
```

### `POST /api/v1/bookings`
- Body JSON:
  ```json
  {
    "studentProfileId": "...",
    "tutorProfileId": "...",
    "subjectId": "...",
    "parentProfileId": "...",   // nullable
    "startTime": "2025-09-30T12:00:00Z",
    "durationMinutes": 60,
    "totalFee": 450000.0,
    "meetingLink": "https://meet...",
    "notes": "..."
  }
  ```
- Tr? `201` + `BookingResponse`.
- Quy?n:
  - Admin: t?o thay b?t k?.
  - Parent: ch? du?c dùng parent profile c?a chính mình + ph?i liên k?t v?i student.
  - Student: ch? d?t cho chính mình (optional parent n?u liên k?t).
- L?i: `400` n?u gi? quá kh?/phí <= 0; `403` n?u không d? quy?n; `404` n?u profile/subject không t?n t?i.

### `POST /api/v1/bookings/{bookingId}/confirm`
- Ch? tutor s? h?u booking (ho?c admin) m?i du?c confirm.
- Booking ph?i ? tr?ng thái `REQUESTED`.
- Tr? `200` + `BookingResponse`.
- L?i: `400` (tr?ng thái khác), `403` (không ph?i tutor), `404` (không tìm th?y).

### `POST /api/v1/bookings/{bookingId}/cancel`
- Ngu?i du?c phép: admin, tutor s? h?u, student chính ch?, parent du?c liên k?t.
- Ch? hu? khi tr?ng thái `REQUESTED` ho?c `CONFIRMED`.
- Tr? `200` + `BookingResponse`.

### `POST /api/v1/bookings/{bookingId}/complete`
- Ch? tutor s? h?u (ho?c admin) và booking ph?i `CONFIRMED`.
- Tr? `200` + `BookingResponse` c?p nh?t sang `COMPLETED`.

### `GET /api/v1/bookings/tutor/{tutorProfileId}`
- Li?t kê l?ch c?a tutor.
- Ch? tutor dó ho?c admin truy c?p; ph? huynh/h?c sinh b? c?m (`403`).
- Tr? `200` + `[]` `BookingResponse`.

### `GET /api/v1/bookings/student/{studentProfileId}`
- Li?t kê l?ch c?a h?c sinh.
- Quy?n: admin, chính h?c sinh, ho?c parent liên k?t.
- Tr? `200` + danh sách booking.

## L?i chung
- `401 Unauthorized`: thi?u ho?c sai JWT.
- `403 Forbidden`: có JWT nhung không d? quy?n.
- `404 Not Found`: tài nguyên không t?n t?i.
- `409 Conflict`: d? li?u trùng (ví d? email dang ký).

## Ghi chú tri?n khai client
- Flutter luu JWT b?ng `shared_preferences`, g?i lên header `Authorization`.
- Android Java module (Retrofit) có interceptor chèn JWT, tr? v? `IllegalStateException` khi l?i ? c?n map sang UI phù h?p.
- Luôn ki?m tra mã l?i d? hi?n th? thông báo dúng (401 vs 403 vs 400).

C?p nh?t tài li?u này m?i khi endpoint thay d?i.
# Tutor Booking REST API

Phi�n b?n: 1.0.0  
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
- Tr? v? `201` + `AuthResponse` (JWT + th�ng tin t�i kho?n).
- L?i ph? bi?n: `400` (sai d? li?u), `409` (email d� t?n t?i).

### `POST /api/v1/auth/login`
- Body JSON:
  ```json
  {
    "email": "parent@example.com",
    "password": "Password123"
  }
  ```
- Tr? v? `200` + `AuthResponse`.
- L?i: `401` n?u sai th�ng tin.

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
T?t c? endpoint du?i d�y y�u c?u header `Authorization: Bearer <token>`.

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
  - Parent: ch? du?c d�ng parent profile c?a ch�nh m�nh + ph?i li�n k?t v?i student.
  - Student: ch? d?t cho ch�nh m�nh (optional parent n?u li�n k?t).
- L?i: `400` n?u gi? qu� kh?/ph� <= 0; `403` n?u kh�ng d? quy?n; `404` n?u profile/subject kh�ng t?n t?i.

### `POST /api/v1/bookings/{bookingId}/confirm`
- Ch? tutor s? h?u booking (ho?c admin) m?i du?c confirm.
- Booking ph?i ? tr?ng th�i `REQUESTED`.
- Tr? `200` + `BookingResponse`.
- L?i: `400` (tr?ng th�i kh�c), `403` (kh�ng ph?i tutor), `404` (kh�ng t�m th?y).

### `POST /api/v1/bookings/{bookingId}/cancel`
- Ngu?i du?c ph�p: admin, tutor s? h?u, student ch�nh ch?, parent du?c li�n k?t.
- Ch? hu? khi tr?ng th�i `REQUESTED` ho?c `CONFIRMED`.
- Tr? `200` + `BookingResponse`.

### `POST /api/v1/bookings/{bookingId}/complete`
- Ch? tutor s? h?u (ho?c admin) v� booking ph?i `CONFIRMED`.
- Tr? `200` + `BookingResponse` c?p nh?t sang `COMPLETED`.

### `GET /api/v1/bookings/tutor/{tutorProfileId}`
- Li?t k� l?ch c?a tutor.
- Ch? tutor d� ho?c admin truy c?p; ph? huynh/h?c sinh b? c?m (`403`).
- Tr? `200` + `[]` `BookingResponse`.

### `GET /api/v1/bookings/student/{studentProfileId}`
- Li?t k� l?ch c?a h?c sinh.
- Quy?n: admin, ch�nh h?c sinh, ho?c parent li�n k?t.
- Tr? `200` + danh s�ch booking.

## L?i chung
- `401 Unauthorized`: thi?u ho?c sai JWT.
- `403 Forbidden`: c� JWT nhung kh�ng d? quy?n.
- `404 Not Found`: t�i nguy�n kh�ng t?n t?i.
- `409 Conflict`: d? li?u tr�ng (v� d? email dang k�).

## Ghi ch� tri?n khai client
- Flutter luu JWT b?ng `shared_preferences`, g?i l�n header `Authorization`.
- Android Java module (Retrofit) c� interceptor ch�n JWT, tr? v? `IllegalStateException` khi l?i ? c?n map sang UI ph� h?p.
- Lu�n ki?m tra m� l?i d? hi?n th? th�ng b�o d�ng (401 vs 403 vs 400).

C?p nh?t t�i li?u n�y m?i khi endpoint thay d?i.
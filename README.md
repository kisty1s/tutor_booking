# Tutor Booking

A professional platform for booking tutors, supporting web, Android, and Flutter mobile clients.

## Features
- User authentication & authorization
- Tutor and parent profiles
- Subject management
- Booking system
- RESTful API
- Mobile clients (Android, Flutter)

## Getting Started

### Prerequisites
- Java 17
- Maven
- Android Studio (for Android client)
- Flutter SDK (for Flutter client)

### Database & JWT Configuration
- Cấu hình DB: Sửa `src/main/resources/application.yml` với thông tin SQL Server, ví dụ:
	```yaml
	spring:
		datasource:
			url: jdbc:sqlserver://localhost:1433;databaseName=tutor_booking
			username: <db_user>
			password: <db_password>
	```
- Cấu hình JWT: Thêm vào `application.yml`:
	```yaml
	jwt:
		secret: <your_jwt_secret>
		expiration: 86400
	```

### Backend Setup
```bash
cd tutor-booking
mvn clean install
mvn spring-boot:run
```

### Mobile Setup
- Android: Open with Android Studio, build & run.
- Flutter: Run `flutter pub get` then `flutter run` in `mobile/flutter_app`.


## Development Workflow
- Tạo nhánh mới cho mỗi tính năng/sửa lỗi: `git checkout -b feature/ten-tinh-nang`
- Đặt tên commit rõ ràng, ngắn gọn, tiếng Anh hoặc Việt có dấu.
- Mở PR, yêu cầu review chéo (ít nhất 1 thành viên khác).
- Không push trực tiếp lên main.
- Checklist PR: cập nhật docs/REST-API.md, client models nếu thay đổi contract.
- Đảm bảo đã chạy test (`mvn test`, `flutter test`) trước khi merge.

## Contributing
See [CONTRIBUTING.md](CONTRIBUTING.md)

## License
See [LICENSE](LICENSE)

## Maintainers
- kisty1s

## Contact
Open an issue or pull request for support.

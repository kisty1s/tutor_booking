## Quy trình làm việc nhóm chuyên nghiệp

1. **Tạo nhánh mới** cho mỗi tính năng hoặc sửa lỗi:
	```bash
	git checkout -b feature/ten-tinh-nang
	```
2. **Commit và push** lên nhánh vừa tạo:
	```bash
	git add .
	git commit -m "Mô tả thay đổi"
	git push origin feature/ten-tinh-nang
	```
3. **Tạo Pull Request (PR)** lên nhánh `main` trên GitHub.
4. **Review code**: Thành viên khác kiểm tra, góp ý và phê duyệt.
5. **Merge vào main** khi đã được review và kiểm tra kỹ.

# Pull Request Template

## Mô tả thay đổi
<!-- Mô tả chi tiết về thay đổi của bạn -->

## Liên quan đến Issue
<!-- Đề cập đến Issue liên quan nếu có -->

## Động lực và bối cảnh
<!-- Tại sao cần thay đổi này? -->

## Cách kiểm tra
<!-- Hướng dẫn kiểm tra thay đổi này -->

## Ảnh chụp màn hình (nếu có)

## Loại thay đổi
- [ ] Sửa lỗi
- [ ] Tính năng mới
- [ ] Thay đổi lớn
- [ ] Cập nhật tài liệu


## Checklist
- [ ] Code tuân thủ chuẩn dự án
- [ ] Đã thêm test cho thay đổi
- [ ] Tất cả test đều pass
- [ ] Đã cập nhật docs/REST-API.md nếu có thay đổi contract
- [ ] Đã cập nhật client models (mobile/web) nếu có thay đổi contract
- [ ] Đã cập nhật tài liệu API (README/docs)
- [ ] Đã chạy `mvn test` (backend) và/hoặc `flutter test` (mobile)
